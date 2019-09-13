package ru.protei.portal.ui.document.client.activity.form;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.DocumentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DocumentControllerAsync;
import ru.protei.portal.ui.common.client.widget.document.uploader.UploadHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;

import java.util.List;
import java.util.Objects;

public abstract class DocumentFormActivity
        implements Activity, AbstractDocumentFormActivity {

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        this.authorizedProfile = event.profile;
    }

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.documentUploader().setUploadHandler(new UploadHandler() {
            @Override
            public void onError() {
                fireErrorMessage(lang.errSaveDocumentFile());
            }
            @Override
            public void onSuccess() {
                saveUploadedDocument();
            }
        });
    }

    @Event
    public void onShow(DocumentEvents.Form.Show event) {
        event.parent.clear();
        event.parent.add(view.asWidget());
        tag = event.tag;
        fillView(event.document);
    }

    @Event
    public void onSave(DocumentEvents.Form.Save event) {
        if (!Objects.equals(tag, event.tag)) {
            return;
        }
        Document newDocument = fillDto();
        if (!checkDocumentUploadValid(newDocument) || !checkDocumentValid(newDocument)) {
            return;
        }
        saveDocument(newDocument);
    }

    @Event
    public void onSetProject(DocumentEvents.Form.SetProject event) {
        if (!Objects.equals(tag, event.tag)) {
            return;
        }
        view.project().setValue(event.project);
        onProjectChanged();
    }

    @Override
    public void onDecimalNumberChanged() {
    }

    @Override
    public void onDocumentCategoryChanged() {
        En_DocumentCategory category = view.documentCategory().getValue();

        view.documentTypeEnabled().setEnabled(category != null);

        if (category == null) {
            view.equipmentVisible().setVisible(false);
            return;
        }

        if( view.documentType().getValue() != null && !category.equals( view.documentType().getValue().getDocumentCategory())){
            view.documentType().setValue(null);
        }
        view.setDocumentTypeCategoryFilter(documentType -> documentType.getDocumentCategory() == category );

        view.equipmentVisible().setVisible(category.isForEquipment());

        setDecimalNumberEnabled();
        setDesignationVisibility();

    }

    @Override
    public void onProjectChanged() {
        ProjectInfo project = view.project().getValue();
        view.equipmentEnabled().setEnabled(project != null);
        view.equipment().setValue(null, true);

        if (project != null)
            view.setEquipmentProjectId(project.getId());

        setDesignationVisibility();
    }

    @Override
    public void onEquipmentChanged() {
        view.decimalNumber().setValue(null, true);
        EquipmentShortView equipment = view.equipment().getValue();
        if (equipment == null) {
            setDecimalNumberEnabled();
        } else {
            List<DecimalNumber> decimalNumbers = equipment.getDecimalNumbers();
            view.decimalNumberEnabled().setEnabled(true);
            view.setDecimalNumberHints(decimalNumbers);
        }
    }

    private void setDesignationVisibility() {
        boolean isDesignationVisible = isDesignationVisible();
        view.decimalNumberVisible().setVisible(isDesignationVisible);
        view.inventoryNumberVisible().setVisible(isDesignationVisible);
    }

    private boolean isDesignationVisible() {
        En_DocumentCategory documentCategory = view.documentCategory().getValue();
        ProjectInfo project = view.project().getValue();

        if (project == null || documentCategory == null || documentCategory == En_DocumentCategory.ABROAD)
            return false;

        return project.getCustomerType() == En_CustomerType.MINISTRY_OF_DEFENCE ||
                project.getCustomerType() == En_CustomerType.STATE_BUDGET;
    }

    private void setDecimalNumberEnabled() {
        En_DocumentCategory category = view.documentCategory().getValue();
        view.decimalNumberEnabled().setEnabled(category != null && !category.isForEquipment());
    }

    private boolean checkDocumentUploadValid(Document newDocument) {
        if (newDocument.getId() == null && !view.documentUploader().isFileSet()) {
            fireErrorMessage(lang.uploadingDocumentNotSet());
            return false;
        }
        return true;
    }

    private boolean checkDocumentValid(Document newDocument) {
        if (!isValidDocument(newDocument)) {
            fireErrorMessage(getValidationErrorMessage(newDocument));
            return false;
        }
        return true;
    }

    private boolean isValidDocument(Document document){
        return document.isValid() && isValidInventoryNumberForMinistryOfDefence(document);
    }

    private boolean isValidInventoryNumberForMinistryOfDefence(Document document) {
        if (view.project().getValue().getCustomerType() == En_CustomerType.MINISTRY_OF_DEFENCE) {
            return document.getInventoryNumber() != null && (document.getInventoryNumber() > 0);
        }
        return true;
    }

    private void saveDocument(Document document) {
        this.document = document;
        if (document.getId() == null)
            view.documentUploader().uploadBindToDocument(document);
        else
            saveUploadedDocument();
    }

    private void fireErrorMessage(String msg) {
        fireEvent(new NotifyEvents.Show(msg, NotifyEvents.NotifyType.ERROR));
    }

    private void saveUploadedDocument() {
        documentService.saveDocument(this.document, new FluentCallback<Document>()
            .withErrorMessage(lang.errDocumentNotSaved())
            .withSuccess(result -> fireEvent(new DocumentEvents.Form.Saved(tag))));
    }

    private String getValidationErrorMessage(Document doc) {
        if (doc.getType() == null) {
            return lang.documentTypeIsEmpty();
        }
        if (doc.getProjectId() == null) {
            return lang.documentProjectIsEmpty();
        }
        if (doc.getInventoryNumber() == null || doc.getInventoryNumber() == 0) {
            return lang.inventoryNumberIsEmpty();
        } else if (doc.getInventoryNumber() < 0) {
            return lang.negativeInventoryNumber();
        }
        if (HelperFunc.isEmpty(doc.getName())) {
            return lang.documentNameIsNotSet();
        }
        return null;
    }

    private Document fillDto() {
        Document d = new Document();
        d.setId(document.getId());
        d.setName(view.name().getValue());
        d.setAnnotation(view.annotation().getValue());
        d.setDecimalNumber(StringUtils.nullIfEmpty(view.decimalNumberText().getText()));
        d.setType(view.documentType().getValue());
        d.setExecutionType(view.executionType().getValue());
        d.setInventoryNumber(view.inventoryNumber().getValue());
        d.setKeywords(view.keywords().getValue());
        d.setContractor(Person.fromPersonShortView(view.contractor().getValue()));
        d.setRegistrar(Person.fromPersonShortView(view.registrar().getValue()));
        d.setVersion(view.version().getValue());
        d.setProjectId(view.project().getValue() == null? null : view.project().getValue().getId());
        d.setEquipment(view.equipment().getValue() == null ? null : new Equipment(view.equipment().getValue().getId()));
        d.setApproved(view.isApproved().getValue());
        d.setState(document.getState());
        return d;
    }

    private void fillView(Document document) {
        this.document = document;

        boolean isNew = document.getId() == null;

        view.name().setValue(document.getName());
        view.annotation().setValue(document.getAnnotation());
        view.executionType().setValue(document.getExecutionType());
        view.documentCategory().setValue(document.getType() == null ? null : document.getType().getDocumentCategory());
        view.documentType().setValue(document.getType());
        view.inventoryNumber().setValue(document.getInventoryNumber());
        view.keywords().setValue(document.getKeywords());
        view.project().setValue(document.getProjectInfo());
        view.version().setValue(document.getVersion());
        view.equipment().setValue(EquipmentShortView.fromEquipment(document.getEquipment()));
        view.decimalNumberText().setText(document.getDecimalNumber());
        view.isApproved().setValue(isNew ? false : document.getApproved());

        if (isNew) {
            PersonShortView currentPerson = new PersonShortView(authorizedProfile.getShortName(), authorizedProfile.getId(), authorizedProfile.isFired());
            view.registrar().setValue(currentPerson);
            view.contractor().setValue(currentPerson);
        } else {
            view.registrar().setValue(document.getRegistrar() == null ? null : document.getRegistrar().toShortNameShortView());
            view.contractor().setValue(document.getContractor() == null ? null : document.getContractor().toShortNameShortView());
        }

        boolean decimalNumberIsNotSet = StringUtils.isEmpty(document.getDecimalNumber());
        boolean inventoryNumberIsNotSet = document.getInventoryNumber() == null;

        view.uploaderVisible().setVisible(isNew);
        view.equipmentEnabled().setEnabled(isNew || decimalNumberIsNotSet);
        view.decimalNumberEnabled().setEnabled(decimalNumberIsNotSet);
        view.inventoryNumberEnabled().setEnabled(inventoryNumberIsNotSet);

        view.nameValidator().setValid(true);

        view.resetFilename();
        view.documentUploader().resetAction();

        setDesignationVisibility();
    }

    @Inject
    Lang lang;
    @Inject
    AbstractDocumentFormView view;
    @Inject
    DocumentControllerAsync documentService;

    private String tag;
    private Document document;
    private Profile authorizedProfile;
}
