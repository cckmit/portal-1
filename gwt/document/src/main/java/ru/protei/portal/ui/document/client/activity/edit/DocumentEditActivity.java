package ru.protei.portal.ui.document.client.activity.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.helper.DocumentUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DocumentControllerAsync;
import ru.protei.portal.ui.common.client.service.EquipmentControllerAsync;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.client.widget.document.uploader.UploadHandler;
import ru.protei.portal.ui.common.client.widget.selector.equipment.EquipmentModel;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;

import java.util.*;
import java.util.function.Consumer;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;

public abstract class DocumentEditActivity
        implements Activity, AbstractDocumentEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onCreateFromWizard(DocumentEvents.CreateFromWizard event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.DOCUMENT_CREATE)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(event.parent));
            return;
        }

        fireBackEvent = () -> fireEvent(new DocumentEvents.Show(false));
        placeView(event.parent);
        view.drawInWizardContainer(true);
        this.document = new Document();
        fillView(document);
    }

    @Event(Type.FILL_CONTENT)
    public void onEquipmentCreate(DocumentEvents.CreateWithEquipment event){
        if (!policyService.hasPrivilegeFor(En_Privilege.EQUIPMENT_EDIT)){
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }
        if (event.projectId == null || event.equipmentId == null) {
            fireEvent(new NotifyEvents.Show(lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR));
            fireEvent(new Back());
            return;
        }

        fireBackEvent = () -> fireEvent(new Back());

        placeView(initDetails.parent);
        view.drawInWizardContainer(false);
        Document document = makeDocumentFromEvent(event);
        this.document = document;
        requestEquipmentAndFillView(event.equipmentId, document);
    }

    @Event(Type.FILL_CONTENT)
    public void onEdit(DocumentEvents.Edit event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.DOCUMENT_EDIT) && !policyService.hasPrivilegeFor(En_Privilege.EQUIPMENT_EDIT)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }

        fireBackEvent =
                event.backEvent == null ?
                () -> fireEvent(new Back()) :
                event.backEvent;

        placeView(initDetails.parent);
        view.drawInWizardContainer(false);
        requestDocumentAndFillView(event.id,
                doc -> {
                    this.document = doc;
                    fillView(doc);
                });
    }

    @Event
    public void onSave(DocumentEvents.Save event) {
        onSaveClicked();
    }

    @Event
    public void onSaveAndContinue(DocumentEvents.SaveAndContinue event) {
        setButtonsEnabled(false);
        saveDocument(fillDto(document), true);
    }

    @Event
    public void onSetProject(ProjectEvents.Set event) {
        if (event.project != null) {
            view.project().setValue(event.project);
            onProjectChanged();
        }
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
            }
        });
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
    public void onDownloadApproved() {
        if (document == null) return;
        Window.open(DOWNLOAD_PATH + document.getProjectId() + "/" + document.getId() + "/as", document.getName(), "");
    }

    @Override
    public void onApprovedChanged() {
        setApprovedByEnable(view.isApproved().getValue());
        setApprovalDateEnable(view.isApproved().getValue());
        setUploaderApprovalSheetEnable(view.isApproved().getValue());
        renderViewState(project);
        view.setApprovalFieldsMandatory(view.isApproved().getValue());
    }

    @Override
    public void onCancelClicked() {
        fireBackEvent.run();
    }

    @Override
    public void onSaveClicked () {
            setButtonsEnabled(false);
            saveDocument(fillDto(document));
    }

    private void onProjectChanged(ProjectInfo project) {
        this.project = project;
        fillViewProjectInfo(project);
        renderViewState(project);
        view.setEquipmentProjectIds(project == null || project.getId() == null ? Collections.emptySet() : EquipmentModel.makeProjectIds(project.getId()));
    }

    private Document makeDocumentFromEvent(DocumentEvents.CreateWithEquipment event) {
        Document document = new Document();
        document.setApproved(false);
        document.setProjectId(event.projectId);
        document.setProjectName(event.projectName);
        return document;
    }

    private Document makeDocumentToContinue() {
        Document document = new Document();
        document.setProjectId(view.project().getValue() == null? null : view.project().getValue().getId());
        document.setProjectName(view.project().getValue() == null? null : view.project().getValue().getDisplayText());
        document.setName(view.name().getValue());
        document.setAnnotation(view.annotation().getValue());
        document.setKeywords(new ArrayList<>(view.keywords().getValue()));
        return document;
    }

    private void placeView(HasWidgets container) {
        container.clear();
        Window.scrollTo(0, 0);
        container.add(view.asWidget());
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
        view.decimalNumberEnabled(isEnableDecimalNumberByApproved(document) && isDesignationEnabled);
        view.inventoryNumberEnabled(isEnableInventoryNumberByApproved(document) && isDesignationEnabled);
        setInventoryNumberMandatory(DocumentUtils.needToCheckInventoryNumber(project,
                view.isApproved().getValue(), view.documentType().getValue()));
        setUploaderEnabled(isNew || !view.isApproved().getValue() || !document.getApproved());
    }

    private void setInventoryNumberMandatory(boolean isMandatory) {
        view.setInventoryNumberMandatory(isMandatory);
        view.setDecimalNumberHMandatory(isMandatory);
    }
    private void setEquipmentEnabled(boolean isEnabled) {
        view.equipmentEnabled(isEnabled);
    }
    private void setDocumentTypeEnabled(boolean isEnabled) {
        view.documentTypeEnabled(isEnabled);
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
        boolean isValid = (document.getId() == null) ?
                DocumentUtils.isValidNewDocument(
                        newDocument,
                        project,
                        view.documentDocUploader().isFileSet(),
                        view.documentPdfUploader().isFileSet())
                : DocumentUtils.isValidDocument(
                        newDocument,
                        project);

        if (!isValid) {
            fireErrorMessage(getValidationErrorMessage(newDocument));
        }

        return isValid;
    }

    private void saveDocument(Document document) {
        saveDocument(document, false);
    }

    private void saveDocument(Document document, boolean stayOnPage) {
        if (!checkDocumentValid(document)) {
            setButtonsEnabled(true);
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
                            this.document = doc;
                            setButtonsEnabled(true);
                            fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                            if (stayOnPage){
                                fillView(makeDocumentToContinue());
                            } else {
                                fireBackEvent.run();
                            }
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
                .withError(throwable -> {
                    setButtonsEnabled(true);
                    errorHandler.accept(throwable);
                })
                .withSuccess(onSaved));
    }

    private String getValidationErrorMessage(Document doc) {
        if (HelperFunc.isEmpty(doc.getName())) {
            return lang.documentNameIsNotSet();
        }

        if (doc.getType() == null && (view.documentCategory() == null || view.documentCategory().getValue() == null)) {
            return lang.documentCategoryIsEmpty();
        }

        if (doc.getType() == null) {
            return lang.documentTypeIsEmpty();
        }

        if (doc.getProjectId() == null) {
            return lang.documentProjectIsEmpty();
        }
        if (DocumentUtils.needToCheckInventoryNumber(project,
                view.isApproved().getValue(), view.documentType().getValue())) {
            if (doc.getInventoryNumber() == null || doc.getInventoryNumber() == 0) {
                return lang.inventoryNumberIsEmpty();
            } else if (doc.getInventoryNumber() < 0) {
                return lang.negativeInventoryNumber();
            }
        }

        if (!DocumentUtils.isValidDecimalNumber(doc)) {
            return lang.decimalNumberIsInvalid();
        }

        if (doc.getApproved()) {
            if (doc.getApprovedBy() == null || doc.getApprovalDate() == null) {
                return lang.documentApproveFieldsIsEmpty();
            }

            if (!view.documentPdfUploader().isFileSet()) {
                return lang.documentPDFFileIsNotSet();
            }
        }
        return null;
    }

    private void requestEquipmentAndFillView(Long equipmentId, Document document) {

        equipmentController.getEquipment(equipmentId, new FluentCallback<Equipment>()
                .withError(throwable -> {
                    errorHandler.accept(throwable);
                    fillView(document);
                })
                .withSuccess(equipment -> {
                    document.setEquipment(equipment);
                    fillView(document);;
                })
        );
    }

    private void requestDocumentAndFillView(Long documentId, Consumer<Document> onSuccess) {
        documentService.getDocument(documentId, new FluentCallback<Document>()
                .withError(throwable -> {
                    errorHandler.accept(throwable);
                    fireEvent(new Back());
                })
                .withSuccess(onSuccess));
    }

    private Document fillDto(Document d) {
        d.setName(view.name().getValue());
        d.setAnnotation(view.annotation().getValue());
        d.setDecimalNumber(StringUtils.nullIfEmpty(view.decimalNumberText().getText()));
        d.setType(view.documentType().getValue());
        d.setMembers(listOf(view.members().getValue()));
        d.setExecutionType(view.executionType().getValue());
        d.setInventoryNumber(view.inventoryNumber().getValue());
        d.setKeywords(view.keywords().getValue());
        d.setContractor(view.contractor().getValue());
        d.setRegistrar(view.registrar().getValue());
        d.setVersion(view.version().getValue());
        d.setProjectId(view.project().getValue() == null? null : view.project().getValue().getId());
        d.setEquipment(view.equipment().getValue() == null ? null : new Equipment(view.equipment().getValue().getId()));
        d.setApproved(view.isApproved().getValue());
        d.setApprovedBy(view.approvedBy().getValue());
        d.setApprovalDate(view.approvalDate().getValue());
        d.setState(document.getState());
        return d;
    }

    private void fillView(Document document) {
        boolean isNew = isNew(document);

        view.setDownloadCloudsVisible(!isNew);
        view.name().setValue(document.getName());
        view.annotation().setValue(document.getAnnotation());
        view.executionType().setValue(document.getExecutionType());
        view.setDocumentCategoryValue(availableDocumentCategories);
        view.documentCategory().setValue(document.getType() == null ? null : document.getType().getDocumentCategory());
        view.documentType().setValue(document.getType());
        view.members().setValue( setOf(document.getMembers()));
        view.keywords().setValue(document.getKeywords());
        view.version().setValue(document.getVersion());
        view.inventoryNumber().setValue(document.getInventoryNumber());
        view.equipment().setValue(EquipmentShortView.fromEquipment(document.getEquipment()));
        view.decimalNumberText().setText(document.getDecimalNumber());
        view.isApproved().setValue(isNew ? false : document.getApproved());
        view.nameValidator().setValid(!view.name().getValue().isEmpty());
        view.documentDocUploader().resetForm();
        view.documentPdfUploader().resetForm();
        view.documentApprovalSheetUploader().resetForm();
        view.setApprovalFieldsMandatory(view.isApproved().getValue());
        setUploaderApprovalSheetEnable(view.isApproved().getValue());

        boolean isApproved = view.isApproved().getValue();
        view.approvedBy().setValue(!isApproved || document.getApprovedBy() == null ? null : document.getApprovedBy());
        view.approvalDate().setValue(!isApproved ? null : document.getApprovalDate());
        view.approvedByEnabled(isApproved);
        view.approvalDateEnabled(isApproved);

        if (isNew) {
            Profile profile = policyService.getProfile();
            PersonShortView currentPerson = new PersonShortView(null, profile.getId(), profile.isFired());
            currentPerson.setDisplayShortName( profile.getShortName() );
            view.registrar().setValue(currentPerson);
            view.contractor().setValue(currentPerson);
            view.inventoryNumberEnabled(true);
            view.decimalNumberEnabled(true);
        } else {
            view.registrar().setValue(document.getRegistrar() == null ? null : document.getRegistrar());
            view.contractor().setValue(document.getContractor() == null ? null : document.getContractor());
            view.inventoryNumberEnabled(isEnableInventoryNumberByApproved(this.document));
            view.decimalNumberEnabled(isEnableDecimalNumberByApproved(this.document));
        }

        view.project().setValue(document.getProjectId() == null ? null : new EntityOption(document.getProjectName(), document.getProjectId()));

        if (document.getProjectId() == null) {
            onProjectChanged(null);
        } else {
            requestProject(document.getProjectId(), this::onProjectChanged);
        }

        view.projectEnabled(isNew);

        if (document.getEquipment() != null && document.getEquipment().getDecimalNumbers() != null) {
            view.setDecimalNumberHints(document.getEquipment().getDecimalNumbers());
        }

        view.membersEnabled(policyService.hasAnyPrivilegeOf(En_Privilege.DOCUMENT_CREATE, En_Privilege.DOCUMENT_EDIT));

    }

    private void fillViewProjectInfo(ProjectInfo project) {
        view.setProjectInfo(
            project == null ? "" : customerTypeLang.getName(project.getCustomerType()),
            project == null ? "" : joining(project.getProductDirection(), ", ", EntityOption::getDisplayText),
            project == null ? "" : fetchDisplayText(project.getRegion())
        );
    }

    private String fetchDisplayText(EntityOption option) {
        if (option == null) {
            return "";
        }
        return option.getDisplayText();
    }

    private void setButtonsEnabled (boolean isEnabled){
        view.setButtonsEnabled(isEnabled);
        fireEvent(new DocumentEvents.SetButtonsEnabled(isEnabled));
    }

    private Boolean isEnableInventoryNumberByApproved(Document doc) {
        return (doc == null) || (doc.getApproved() == null || !doc.getApproved()) || doc.getInventoryNumber() == null;
    }
    private Boolean isEnableDecimalNumberByApproved(Document doc) {
        return (doc == null) || (doc.getApproved() == null || !doc.getApproved()) || doc.getDecimalNumber() == null;
    }

    private boolean isNew(Document document) {
        return document == null || document.getId() == null;
    }

    @Inject
    Lang lang;
    @Inject
    En_CustomerTypeLang customerTypeLang;
    @Inject
    AbstractDocumentEditView view;
    @Inject
    DocumentControllerAsync documentService;
    @Inject
    RegionControllerAsync regionService;
    @Inject
    PolicyService policyService;
    @Inject
    DefaultErrorHandler errorHandler;
    @Inject
    EquipmentControllerAsync equipmentController;

    private Document document;
    private ProjectInfo project;
    private static final String DOWNLOAD_PATH = GWT.getModuleBaseURL() + "springApi/download/document/";
    private AppEvents.InitDetails initDetails;
    private List<En_DocumentCategory> availableDocumentCategories = new ArrayList<>(Arrays.asList(En_DocumentCategory.values()));
    private Runnable fireBackEvent = () -> fireEvent(new Back());
}
