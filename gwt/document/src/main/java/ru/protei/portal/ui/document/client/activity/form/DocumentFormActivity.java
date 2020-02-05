package ru.protei.portal.ui.document.client.activity.form;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
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
import java.util.stream.Collectors;

public abstract class DocumentFormActivity
        implements Activity, AbstractDocumentFormActivity {

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
    }

    @Event
    public void onSave(DocumentEvents.Form.Save event) {
        if (!Objects.equals(tag, event.tag)) {
            return;
        }
        if (!isDocumentCreationInProgress) {
            isDocumentCreationInProgress = true;
            saveDocument(fillDto(document));
        }
    }

    @Event
    public void onSetProject(DocumentEvents.Form.SetProject event) {
        if (!Objects.equals(tag, event.tag)) {
            return;
        }

        if (event.project != null) {
            view.project().setValue(event.project);
            onProjectChanged();
        }
    }

    @Override
    public void onDecimalNumberChanged() {
    }

    @Override
    public void onDocumentCategoryChanged() {
        En_DocumentCategory documentCategory = view.documentCategory().getValue();
        DocumentType documentType = view.documentType().getValue();
        boolean isDocumentTypeSet = documentType != null;
        boolean isDocumentTypeCategoryMatched = isDocumentTypeSet && Objects.equals(documentCategory, documentType.getDocumentCategory());

        if (!isDocumentTypeCategoryMatched) {
            view.documentType().setValue(null);
        }
        view.setDocumentTypeCategoryFilter(type -> type.getDocumentCategory() == documentCategory);

        renderViewState(project);
    }

    @Override
    public void onProjectChanged() {
        if (view.project().getValue() == null) {
            onProjectChanged(null);
            return;
        }
        requestProject(view.project().getValue().getId(), projectInfo -> {
            onProjectChanged(projectInfo);
            if (projectInfo != null) {
                view.equipment().setValue(null, true);
                view.setEquipmentProjectId(projectInfo.getId());
            }
        });
    }

    private void onProjectChanged(ProjectInfo project) {
        this.project = project;
        fillViewProjectInfo(project);
        renderViewState(project);
    }

    @Override
    public void onEquipmentChanged() {
        view.decimalNumber().setValue(null, true);
        EquipmentShortView equipment = view.equipment().getValue();
        if (equipment != null) {
            List<DecimalNumber> decimalNumbers = equipment.getDecimalNumbers();
            view.setDecimalNumberHints(decimalNumbers);
        }
        renderViewState(project);
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

    @Override
    public void onApprovedChanged() {
        setApprovedByEnable(view.isApproved().getValue());
        setApprovalDateEnable(view.isApproved().getValue());
        setUploaderApprovalSheetEnable(view.isApproved().getValue());
        renderViewState(project);
    }

    private void requestProject(long projectId, Consumer<ProjectInfo> consumer) {
        regionService.getProjectInfo(projectId, new FluentCallback<ProjectInfo>()
                .withSuccess(consumer));
    }

    private void renderViewState(ProjectInfo project) {

        boolean isNew = document.getId() == null;
        En_DocumentCategory documentCategory = view.documentCategory().getValue();

        boolean isDesignationEnabled = isDecimalAndInventoryNumbersVisible(project, documentCategory);
        boolean isEquipmentEnabled = isEquipmentVisible(project, documentCategory);

        setEquipmentEnabled(isEquipmentEnabled);
        setDocumentTypeEnabled(documentCategory != null);
        setDecimalNumberEnabled(isDesignationEnabled);
        setInventoryNumberEnabled(isDesignationEnabled);
        setUploaderEnabled(isNew || !view.isApproved().getValue());
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

    private void setApprovedByEnable(boolean isEnabled) {
        view.approvedByEnabled(isEnabled);
        if (!isEnabled) {
            view.approvedBy().setValue(null);
        }
    }

    private void setApprovalDateEnable(boolean isEnabled) {
        view.approvalDateEnabled(isEnabled);
        if (!isEnabled) {
            view.approvalDate().setValue(null);
        }
    }

    private void setUploaderApprovalSheetEnable(boolean isEnabled) {
        view.uploaderApprovalSheetEnabled(isEnabled);
        if (!isEnabled) {
            view.documentApprovalSheetUploader().resetForm();
        }
    }
    private boolean isDecimalAndInventoryNumbersVisible(ProjectInfo project, En_DocumentCategory documentCategory) {
        if (project == null || documentCategory == null || documentCategory == En_DocumentCategory.ABROAD) {
            return false;
        }
        En_CustomerType customerType = project.getCustomerType();
        return customerType == En_CustomerType.MINISTRY_OF_DEFENCE ||
                customerType == En_CustomerType.STATE_BUDGET;
    }
    private boolean isEquipmentVisible(ProjectInfo project, En_DocumentCategory documentCategory) {
        if (project == null || documentCategory == null) {
            return false;
        }
        return documentCategory.isForEquipment();
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
            return document.isValid()
                    && isValidInventoryNumberForMinistryOfDefence(document)
                    && isValidApproveFields(document);
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

    private boolean isValidApproveFields(Document document) {
        if (!document.getApproved()) {
            return true;
        }
        return document.getApprovedBy() != null && document.getApprovalDate() != null;
    }

    private void saveDocument(Document document) {
        if (!checkDocumentValid(document)) {
            isDocumentCreationInProgress = false;
            return;
        }
        this.document = document;
        boolean isPdfFileSet = view.documentPdfUploader().isFileSet();
        boolean isDocFileSet = view.documentDocUploader().isFileSet();
        boolean isApprovedFileSet = view.documentApprovalSheetUploader().isFileSet();
        if (isPdfFileSet || isDocFileSet || isApprovedFileSet) {
            fireEvent(new NotifyEvents.Show(lang.documentSaving(), NotifyEvents.NotifyType.INFO));
        }
        uploadPdf(() ->
            uploadDoc(() ->
                    uploadApprovalSheet(() ->
                        saveDocument(document, doc -> {
                            fillView(doc);
                            isDocumentCreationInProgress = false;
                            fireEvent(new DocumentEvents.Form.Saved(tag));
                        }
        ))));
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

    private void uploadApprovalSheet(Runnable andThen) {
        if (!view.documentApprovalSheetUploader().isFileSet()) {
            andThen.run();
            return;
        }
        view.documentApprovalSheetUploader().setUploadHandler(new UploadHandler() {
            @Override
            public void onError() {
                fireErrorMessage(lang.errSaveDocumentFile());
            }

            @Override
            public void onSuccess() {
                andThen.run();
            }
        });
        view.documentApprovalSheetUploader().uploadBindToDocument(document);
    }

    private void fireErrorMessage(String msg) {
        fireEvent(new NotifyEvents.Show(msg, NotifyEvents.NotifyType.ERROR));
    }

    private void saveDocument(Document document, Consumer<Document> onSaved) {
        documentService.saveDocument(document, new FluentCallback<Document>()
                .withSuccess(onSaved));
    }

    private String getValidationErrorMessage(Document doc) {
        if (doc.getType() == null) {
            return lang.documentTypeIsEmpty();
        }
        if (doc.getProjectId() == null) {
            return lang.documentProjectIsEmpty();
        }
        if (project.getCustomerType() == En_CustomerType.MINISTRY_OF_DEFENCE) {
            if (doc.getInventoryNumber() == null || doc.getInventoryNumber() == 0) {
                return lang.inventoryNumberIsEmpty();
            } else if (doc.getInventoryNumber() < 0) {
                return lang.negativeInventoryNumber();
            }
        }
        if (HelperFunc.isEmpty(doc.getName())) {
            return lang.documentNameIsNotSet();
        }
        if (doc.getApproved()) {
            if (doc.getApprovedBy() == null || doc.getApprovalDate() == null) {
                return lang.documentApproveFieldsIsEmpty();
            }
        }
        return null;
    }

    private Document fillDto(Document d) {
        d.setName(view.name().getValue());
        d.setAnnotation(view.annotation().getValue());
        d.setDecimalNumber(StringUtils.nullIfEmpty(view.decimalNumberText().getText()));
        d.setType(view.documentType().getValue());
        d.setMembers(CollectionUtils.stream(view.members().getValue()).map(Person::fromPersonShortView).collect(Collectors.toList()));
        d.setExecutionType(view.executionType().getValue());
        d.setInventoryNumber(view.inventoryNumber().getValue());
        d.setKeywords(view.keywords().getValue());
        d.setContractor(Person.fromPersonShortView(view.contractor().getValue()));
        d.setRegistrar(Person.fromPersonShortView(view.registrar().getValue()));
        d.setVersion(view.version().getValue());
        d.setProjectId(view.project().getValue() == null? null : view.project().getValue().getId());
        d.setEquipment(view.equipment().getValue() == null ? null : new Equipment(view.equipment().getValue().getId()));
        d.setApproved(view.isApproved().getValue());
        d.setApprovedBy(Person.fromPersonShortView(view.approvedBy().getValue()));
        d.setApprovalDate(view.approvalDate().getValue());
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
        view.members().setValue(CollectionUtils.stream(document.getMembers()).map(PersonShortView::fromPerson).collect(Collectors.toSet()));
        view.keywords().setValue(document.getKeywords());
        view.version().setValue(document.getVersion());
        view.inventoryNumber().setValue(document.getInventoryNumber());
        view.equipment().setValue(EquipmentShortView.fromEquipment(document.getEquipment()));
        view.decimalNumberText().setText(document.getDecimalNumber());
        view.isApproved().setValue(isNew ? false : document.getApproved());
        view.nameValidator().setValid(true);
        view.documentDocUploader().resetForm();
        view.documentPdfUploader().resetForm();
        if (view.isApproved().getValue()) {
            view.approvedBy().setValue(document.getApprovedBy() == null ? null : document.getApprovedBy().toShortNameShortView());
            view.approvalDate().setValue(document.getApprovalDate());
            view.approvedByEnabled(true);
            view.approvalDateEnabled(true);
        } else {
            view.approvedByEnabled(false);
            view.approvalDateEnabled(false);
        }

        if (isNew) {
            Profile profile = policyService.getProfile();
            PersonShortView currentPerson = new PersonShortView(profile.getShortName(), profile.getId(), profile.isFired());
            view.registrar().setValue(currentPerson);
            view.contractor().setValue(currentPerson);
        } else {
            view.registrar().setValue(document.getRegistrar() == null ? null : document.getRegistrar().toShortNameShortView());
            view.contractor().setValue(document.getContractor() == null ? null : document.getContractor().toShortNameShortView());
        }

        view.project().setValue(document.getProjectId() == null ? null : new EntityOption(document.getProjectName(), document.getProjectId()));
        if (document.getProjectId() == null) {
            onProjectChanged(null);
        } else {
            requestProject(document.getProjectId(), this::onProjectChanged);
        }
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
    @Inject
    PolicyService policyService;

    private String tag;
    private Document document;
    private ProjectInfo project;
    private boolean isDocumentCreationInProgress = false;
    private static final String DOWNLOAD_PATH = GWT.getModuleBaseURL() + "springApi/download/document/";
}
