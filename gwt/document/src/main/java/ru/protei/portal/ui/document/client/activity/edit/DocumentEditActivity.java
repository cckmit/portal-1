package ru.protei.portal.ui.document.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.dict.En_DocumentExecutionType;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.DocumentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DocumentControllerAsync;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.document.client.widget.uploader.UploadHandler;

import java.util.List;
import java.util.Objects;


public abstract class DocumentEditActivity
        implements Activity, AbstractDocumentEditActivity {

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
                view.saveEnabled().setEnabled(true);
                fireErrorMessage(lang.errSaveDocumentFile());
            }

            @Override
            public void onSuccess() {
                saveUploadedDocument();
            }
        });
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(DocumentEvents.Edit event) {
        if (event.id == null) {
            fillView(new Document());
            return;
        }

        documentService.getDocument(event.id, new RequestCallback<Document>() {
            @Override
            public void onError(Throwable throwable) {
                fireErrorMessage(lang.errGetObject());
            }

            @Override
            public void onSuccess(Document result) {
                fillView(result);
            }
        });
    }

    @Override
    public void onSaveClicked() {
        if (!view.saveEnabled().isEnabled())
            return;

        Document newDocument = fillDto();
        if (!checkDocumentUploadValid(newDocument) || !checkDocumentValid(newDocument)) {
            return;
        }
        saveDocument(newDocument);
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
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

        view.setDocumentTypeCategoryFilter(category);
        view.equipmentVisible().setVisible(category.isForEquipment());

        setDecimalNumberEnabled();
        setDesignationVisibility();

        if (view.documentType().getValue() != null &&
                !Objects.equals(category, view.documentType().getValue().getDocumentCategory())) {
            view.documentType().setValue(null, true);
        }
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
        if (!newDocument.isValid()) {
            fireErrorMessage(getValidationErrorMessage(newDocument));
            return false;
        }
        return true;
    }

    private void saveDocument(Document document) {
        this.document = document;
        view.saveEnabled().setEnabled(false);
        if (document.getId() == null)
            view.documentUploader().uploadBindToDocument(document);
        else
            saveUploadedDocument();
    }

    private void fireErrorMessage(String msg) {
        fireEvent(new NotifyEvents.Show(msg, NotifyEvents.NotifyType.ERROR));
    }

    private void saveUploadedDocument() {
        documentService.saveDocument(this.document, new RequestCallback<Document>() {
            @Override
            public void onError(Throwable throwable) {
                fireErrorMessage(lang.errDocumentNotSaved());
                view.saveEnabled().setEnabled(true);
            }

            @Override
            public void onSuccess(Document result) {
                fireEvent(new DocumentEvents.ChangeModel());
                fireEvent(new Back());
            }
        });
    }


    private String getValidationErrorMessage(Document doc) {
        if (doc.getType() == null) {
            return lang.documentTypeIsEmpty();
        }
        if (doc.getProjectId() == null) {
            return lang.documentProjectIsEmpty();
        }
        if (doc.getInventoryNumber() != null && doc.getInventoryNumber() < 0) {
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
        d.setApproved(true);
        return d;
    }

    private void fillView(Document document) {
        this.document = document;

        boolean isNew = document.getId() == null;

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        view.name().setValue(document.getName());
        view.annotation().setValue(document.getAnnotation());
        view.executionType().setValue(isNew ? En_DocumentExecutionType.ELECTRONIC : document.getExecutionType());
        view.setCreated( document.getCreated() == null ? "" : lang.documentCreated(DateFormatter.formatDateTime(document.getCreated())));
        view.documentCategory().setValue(document.getType() == null ? null : document.getType().getDocumentCategory(), true);
        view.documentType().setValue(document.getType(), true);
        view.inventoryNumber().setValue(document.getInventoryNumber());
        view.keywords().setValue(document.getKeywords());
        view.project().setValue(document.getProjectInfo(), true);
        view.version().setValue(document.getVersion());
        view.equipment().setValue(EquipmentShortView.fromEquipment(document.getEquipment()), true);
        view.decimalNumberText().setText(document.getDecimalNumber());

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
        view.saveEnabled().setEnabled(true);

        setDesignationVisibility();
    }

    @Inject
    Lang lang;
    @Inject
    AbstractDocumentEditView view;
    @Inject
    DocumentControllerAsync documentService;

    private Document document;
    private Profile authorizedProfile;
    private AppEvents.InitDetails initDetails;
}
