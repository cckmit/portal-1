package ru.protei.portal.ui.document.client.activity.form;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
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
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.DocumentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DocumentControllerAsync;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.client.widget.document.uploader.UploadHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class DocumentFormActivity
        implements Activity, AbstractDocumentFormActivity {

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        this.authorizedProfile = event.profile;
    }

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(DocumentEvents.Form.Show event) {
        event.parent.clear();
        event.parent.add(view.asWidget());
        tag = event.tag;
        fillView(event.document);
        if (view.project().getValue() != null) {
            refreshProject(proj -> {
                setDesignationEnabled(isDesignationVisible(proj, view.documentCategory().getValue()));
                fillViewProjectInfo(proj);
            });
        } else {
            setDesignationEnabled(false);
        }
    }

    @Event
    public void onSave(DocumentEvents.Form.Save event) {
        if (!Objects.equals(tag, event.tag)) {
            return;
        }
        Document newDocument = fillDto(new Document());
        if (!checkDocumentValid(newDocument)) {
            return;
        }
        saveDocument(newDocument);
    }

    @Event
    public void onSetProject(DocumentEvents.Form.SetProject event) {
        if (!Objects.equals(tag, event.tag)) {
            return;
        }
        fillViewProject(ProjectInfo.fromProject(event.project));
        onProjectChanged();
    }

    @Override
    public void onDecimalNumberChanged() {
    }

    @Override
    public void onDocumentCategoryChanged() {
        setDesignationEnabled(isDesignationVisible(project, view.documentCategory().getValue()));

        En_DocumentCategory category = view.documentCategory().getValue();

        setDocumentTypeEnabled(category != null);

        if (category == null) {
            setEquipmentEnabled(false);
            return;
        }

        if( view.documentType().getValue() != null && !category.equals( view.documentType().getValue().getDocumentCategory())){
            view.documentType().setValue(null);
        }
        view.setDocumentTypeCategoryFilter(documentType -> documentType.getDocumentCategory() == category );

        setEquipmentEnabled(category.isForEquipment());

        setDecimalNumberEnabled();
    }

    @Override
    public void onProjectChanged() {
        if (view.project().getValue() != null) {
            refreshProject(proj -> {
                setDesignationEnabled(isDesignationVisible(proj, view.documentCategory().getValue()));
                fillViewProjectInfo(proj);
            });
        } else {
            setDesignationEnabled(false);
        }

        EntityOption project = view.project().getValue();
        setEquipmentEnabled(project != null);
        view.equipment().setValue(null, true);

        if (project != null)
            view.setEquipmentProjectId(project.getId());
    }

    @Override
    public void onEquipmentChanged() {
        view.decimalNumber().setValue(null, true);
        EquipmentShortView equipment = view.equipment().getValue();
        if (equipment == null) {
            setDecimalNumberEnabled();
        } else {
            List<DecimalNumber> decimalNumbers = equipment.getDecimalNumbers();
            setDecimalNumberEnabled(true);
            view.setDecimalNumberHints(decimalNumbers);
        }
    }

    @Override
    public void onDownloadPdf() {
        if (document == null) return;
        Window.open(DOWNLOAD_PATH + document.getProjectId() + "/" + document.getId() + "/pdf", document.getName(), "");
    }

    @Override
    public void onDownloadDoc() {
        if (document == null) return;
        Window.open(DOWNLOAD_PATH + document.getProjectId() + "/" + document.getId() + "/doc", document.getName(), "");
    }

    private void refreshProject(Consumer<ProjectInfo> consumer) {
        regionService.getProjectInfo(view.project().getValue().getId(), new FluentCallback<ProjectInfo>()
                .withSuccess(result -> {
                    project = result;
                    consumer.accept(result);
                })
        );
    }

    private void setDesignationEnabled(boolean isDesignationEnabled) {
        setDecimalNumberEnabled(isDesignationEnabled);
        setInventoryNumberEnabled(isDesignationEnabled);
    }

    private void setDecimalNumberEnabled(boolean isEnabled) {
        view.decimalNumberEnabled(isEnabled);
        if (!isEnabled) {
            view.decimalNumber().setValue(null);
        }
    }

    private void setInventoryNumberEnabled(boolean isEnabled) {
        view.inventoryNumberEnabled(isEnabled);
        if (!isEnabled) {
            view.inventoryNumber().setValue(null);
        }
    }

    private void setEquipmentEnabled(boolean isEnabled) {
        view.equipmentEnabled(isEnabled);
        if (!isEnabled) {
            view.equipment().setValue(null);
        }
    }

    private void setDocumentTypeEnabled(boolean isEnabled) {
        view.documentTypeEnabled(isEnabled);
        if (!isEnabled) {
            view.documentType().setValue(null);
        }
    }

    private void setUploaderEnabled(boolean isEnabled) {
        view.uploaderEnabled(isEnabled);
        if (!isEnabled) {
            view.documentPdfUploader().resetForm();
            view.documentDocUploader().resetForm();
        }
    }

    private boolean isDesignationVisible(ProjectInfo project, En_DocumentCategory documentCategory) {
        if (project == null || documentCategory == null || documentCategory == En_DocumentCategory.ABROAD)
            return false;

        return project.getCustomerType() == En_CustomerType.MINISTRY_OF_DEFENCE ||
                project.getCustomerType() == En_CustomerType.STATE_BUDGET;
    }

    private void setDecimalNumberEnabled() {
        En_DocumentCategory category = view.documentCategory().getValue();
        setDecimalNumberEnabled(category != null && !category.isForEquipment());
    }

    private boolean checkDocumentValid(Document newDocument) {
        if (!isValidDocument(newDocument)) {
            fireErrorMessage(getValidationErrorMessage(newDocument));
            return false;
        }
        return true;
    }

    private boolean isValidDocument(Document document) {
        boolean isNew = document.getId() == null;
        boolean isPdfFileSet = view.documentPdfUploader().isFileSet();
        boolean isDocFileSet = view.documentDocUploader().isFileSet();
        if (isNew && isDocFileSet && !isPdfFileSet) {
            return StringUtils.isNotEmpty(document.getName()) &&
                    document.getProjectId() != null;
        } else {
            return document.isValid() && isValidInventoryNumberForMinistryOfDefence(document);
        }
    }

    private boolean isValidInventoryNumberForMinistryOfDefence(Document document) {
        if (!document.getApproved()) {
            return true;
        }
        if (project.getCustomerType() == En_CustomerType.MINISTRY_OF_DEFENCE) {
            return document.getInventoryNumber() != null && (document.getInventoryNumber() > 0);
        }
        return true;
    }

    private void saveDocument(Document document) {
        this.document = document;
        boolean isPdfFileSet = view.documentPdfUploader().isFileSet();
        boolean isDocFileSet = view.documentDocUploader().isFileSet();
        if (isPdfFileSet || isDocFileSet) {
            fireEvent(new NotifyEvents.Show(lang.documentSaving(), NotifyEvents.NotifyType.INFO));
        }
        uploadPdf(() -> uploadDoc(() -> saveDocument()));
    }

    private void uploadPdf(Runnable andThen) {
        if (!view.documentPdfUploader().isFileSet()) {
            andThen.run();
            return;
        }
        view.documentPdfUploader().setUploadHandler(new UploadHandler() {
            @Override
            public void onError() { fireErrorMessage(lang.errSaveDocumentFile()); }
            @Override
            public void onSuccess() { andThen.run(); }
        });
        view.documentPdfUploader().uploadBindToDocument(document);
    }

    private void uploadDoc(Runnable andThen) {
        if (!view.documentDocUploader().isFileSet()) {
            andThen.run();
            return;
        }
        view.documentDocUploader().setUploadHandler(new UploadHandler() {
            @Override
            public void onError() { fireErrorMessage(lang.errSaveDocumentFile()); }
            @Override
            public void onSuccess() { andThen.run(); }
        });
        view.documentDocUploader().uploadBindToDocument(document);
    }

    private void fireErrorMessage(String msg) {
        fireEvent(new NotifyEvents.Show(msg, NotifyEvents.NotifyType.ERROR));
    }

    private void saveDocument() {
        documentService.saveDocument(this.document, new FluentCallback<Document>()
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

    private Document fillDto(Document d) {
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
        view.keywords().setValue(document.getKeywords());
        fillViewProject(document.getProjectInfo());
        view.version().setValue(document.getVersion());
        view.inventoryNumber().setValue(document.getInventoryNumber());
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

        setUploaderEnabled(isNew || !document.getApproved());
        setEquipmentEnabled(isNew || decimalNumberIsNotSet);
        setDecimalNumberEnabled(decimalNumberIsNotSet);
        setInventoryNumberEnabled(inventoryNumberIsNotSet);

        view.nameValidator().setValid(true);

        view.documentDocUploader().resetForm();
        view.documentPdfUploader().resetForm();
    }

    private void fillViewProject(ProjectInfo project) {
        view.project().setValue(project == null ? null : new EntityOption(project.getName(), project.getId()));
        fillViewProjectInfo(project);
    }

    private void fillViewProjectInfo(ProjectInfo project) {
        view.setProjectInfo(
            project == null ? "" : customerTypeLang.getName(project.getCustomerType()),
            project == null ? "" : fetchDisplayText(project.getProductDirection()),
            project == null ? "" : fetchDisplayText(project.getRegion())
        );
    }

    private String fetchDisplayText(EntityOption option) {
        if (option == null) {
            return "";
        }
        return option.getDisplayText();
    }

    @Inject
    Lang lang;
    @Inject
    En_CustomerTypeLang customerTypeLang;
    @Inject
    AbstractDocumentFormView view;
    @Inject
    DocumentControllerAsync documentService;
    @Inject
    RegionControllerAsync regionService;

    private String tag;
    private Document document;
    private ProjectInfo project;
    private Profile authorizedProfile;
    private static final String DOWNLOAD_PATH = GWT.getModuleBaseURL() + "springApi/download/document/";
}
