package ru.protei.portal.ui.equipment.client.activity.document.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.DecimalNumberFormatter;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EquipmentControllerAsync;
import ru.protei.portal.ui.common.client.widget.document.uploader.UploadHandler;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;

import java.util.List;

public abstract class EquipmentDocumentEditActivity implements Activity, AbstractEquipmentDocumentEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.documentUploader().setUploadHandler(new UploadHandler() {
            @Override
            public void onError() {
                fireEvent(new NotifyEvents.Show(lang.errSaveDocumentFile(), NotifyEvents.NotifyType.ERROR));
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

    @Event(Type.FILL_CONTENT)
    public void onShow(EquipmentEvents.DocumentEdit event) {

        decimalNumbers = null;

        if (event.documentId != null) {
            drawView();
            fireEvent(new AppEvents.InitPanelName(lang.documentEdit()));
            equipmentController.getDocument(event.documentId, new FluentCallback<Document>()
                    .withError(throwable -> {
                        errorHandler.accept(throwable);
                        fireEvent(new Back());
                    })
                    .withSuccess((document, m) -> fillView(document))
            );
            return;
        }

        if (event.projectId == null || event.equipmentId == null) {
            fireEvent(new NotifyEvents.Show(lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR));
            fireEvent(new Back());
            return;
        }

        drawView();
        fireEvent(new AppEvents.InitPanelName(lang.documentCreate()));

        Document document = new Document();
        document.setApproved(false);
        document.setProjectId(event.projectId);
        document.setEquipment(new Equipment(event.equipmentId));
        if (CollectionUtils.isEmpty(event.decimalNumbers)) {
            requestDecimalNumbers(event.equipmentId, () -> fillView(document));
        } else {
            decimalNumbers = event.decimalNumbers;
            fillView(document);
        }
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        authorizedProfile = event.profile;
    }

    @Event
    public void onLogout(AppEvents.Logout event) {
        authorizedProfile = null;
    }

    @Override
    public void onSaveClicked() {

        fillDTO(document);

        if (!checkDocumentUploadValid(document) || !checkDocumentValid(document)) {
            return;
        }

        saveDocument();
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    @Override
    public void onApproveChanged(boolean isApproved) {
        boolean isNew = document.getId() == null;
        view.inventoryNumberEnabled().setEnabled(isApproved);
        if (!isNew) {
            view.setApprovedMode(!isApproved);
        }
    }

    @Override
    public void onDocumentCategoryChanged() {
        En_DocumentCategory category = view.documentCategory().getValue();
        view.documentType().setValue(null, true);
        view.setDocumentTypeCategoryFilter(documentType -> documentType.getDocumentCategory() == category );
        view.documentTypeEnabled().setEnabled(category != null);
    }

    private void requestDecimalNumbers(Long equipmentId, Runnable andThen) {

        if (equipmentId == null) {
            andThen.run();
            return;
        }

        equipmentController.getDecimalNumbersOfEquipment(equipmentId, new FluentCallback<List<DecimalNumber>>()
                .withError(throwable -> {
                    errorHandler.accept(throwable);
                    andThen.run();
                })
                .withSuccess((numbers, m) -> {
                    decimalNumbers = DecimalNumberFormatter.formatNumbersWithoutModification(numbers);
                    andThen.run();
                })
        );
    }

    private void drawView() {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
    }

    private void fillView(Document document) {
        this.document = document;

        boolean isNew = document.getId() == null;
        boolean approveMode = isNew || !document.getApproved();

        view.setApprovedMode(approveMode);
        view.setCreated(isNew ? lang.documentCreate() : document.getCreated() == null ? "" : lang.documentCreated(DateFormatter.formatDateTime(document.getCreated())));
        view.name().setValue(document.getName());
        view.documentUploader().resetAction();
        view.documentUploader().resetForm();
        view.setDocumentUploaderLabel(isNew ? lang.uploadDocuments() : lang.reUploadDocuments());
        view.approved().setValue(document.getApproved());
        view.approvedEnabled().setEnabled(approveMode);
        view.documentCategory().setValue(document.getType() == null ? En_DocumentCategory.TD : document.getType().getDocumentCategory(), true);
        view.documentType().setValue(document.getType(), true);
        view.documentCategoryEnabled().setEnabled(isNew);
        view.documentTypeEnabled().setEnabled(isNew);
        view.version().setValue(document.getVersion());
        view.setDecimalNumbersAvailableValues(decimalNumbers);
        view.decimalNumber().setValue(isNew ? decimalNumbers.get(0) : document.getDecimalNumber());
        view.decimalNumberEnabled().setEnabled(isNew);
        view.inventoryNumber().setValue(document.getApproved() ? document.getInventoryNumber() : null);
        view.inventoryNumberEnabled().setEnabled(document.getApproved());

        PersonShortView registrar;
        PersonShortView contractor;
        if (isNew && authorizedProfile != null) {
            registrar = new PersonShortView(authorizedProfile.getShortName(), authorizedProfile.getId(), authorizedProfile.isFired());
            contractor = new PersonShortView(authorizedProfile.getShortName(), authorizedProfile.getId(), authorizedProfile.isFired());
        } else {
            registrar = document.getRegistrar() == null ? null : document.getRegistrar().toShortNameShortView();
            contractor = document.getContractor() == null ? null : document.getContractor().toShortNameShortView();
        }
        view.registrar().setValue(registrar);
        view.contractor().setValue(contractor);

        view.annotation().setValue(document.getAnnotation());
        view.keywords().setValue(document.getKeywords());
    }

    private void fillDTO(Document document) {
        boolean isNew = document.getId() == null;

        document.setName(view.name().getValue());
        document.setApproved(view.approved().getValue());
        document.setType(view.documentType().getValue());
        document.setVersion(view.version().getValue());
        document.setInventoryNumber(view.approved().getValue() ? view.inventoryNumber().getValue() : null);
        document.setDecimalNumber(isNew ? view.decimalNumber().getValue() + "-" + view.documentType().getValue().getShortName() : view.decimalNumber().getValue());
        document.setContractor(Person.fromPersonShortView(view.contractor().getValue()));
        document.setRegistrar(Person.fromPersonShortView(view.registrar().getValue()));
        document.setAnnotation(view.annotation().getValue());
        document.setKeywords(view.keywords().getValue());
    }

    private void saveDocument() {
        view.saveButtonEnabled().setEnabled(false);
        view.cancelButtonEnabled().setEnabled(false);
        if ((document.getId() == null || !document.getApproved()) && StringUtils.isNotBlank(view.documentUploader().getFilename())) {
            fireEvent(new NotifyEvents.Show(lang.documentSaving(), NotifyEvents.NotifyType.INFO));
            view.documentUploader().uploadBindToDocument(document);
        } else {
            saveUploadedDocument();
        }
    }

    private void saveUploadedDocument() {
        equipmentController.saveDocument(document, new FluentCallback<Document>()
                .withResult(() -> {
                    view.saveButtonEnabled().setEnabled(true);
                    view.cancelButtonEnabled().setEnabled(true);
                })
                .withError(throwable -> {
                    if (throwable instanceof RequestFailedException) {
                        RequestFailedException rf = (RequestFailedException) throwable;
                        if (En_ResultStatus.ALREADY_EXIST.equals(rf.status)) {
                            fireEvent(new NotifyEvents.Show(lang.equipmentDocumentAlreadyExists(), NotifyEvents.NotifyType.ERROR));
                            return;
                        }
                    }
                    errorHandler.accept(throwable);
                })
                .withSuccess((doc, m) -> {
                    fireEvent(new NotifyEvents.Show(lang.documentSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new DocumentEvents.ChangeModel());
                    fireEvent(new Back());
                })
        );
    }

    private boolean checkDocumentUploadValid(Document document) {
        if (document.getId() == null && StringUtils.isBlank(view.documentUploader().getFilename())) {
            fireEvent(new NotifyEvents.Show(lang.uploadingDocumentNotSet(), NotifyEvents.NotifyType.ERROR));
            return false;
        }
        return true;
    }

    private boolean checkDocumentValid(Document document) {
        if (!document.isValid()) {
            fireEvent(new NotifyEvents.Show(getValidationErrorMessage(document), NotifyEvents.NotifyType.ERROR));
            return false;
        }
        return true;
    }

    private String getValidationErrorMessage(Document doc) {
        if (StringUtils.isBlank(doc.getName())) {
            return lang.documentNameIsNotSet();
        }
        if (doc.getType() == null) {
            return lang.documentTypeIsEmpty();
        }
        if (doc.getInventoryNumber() != null && doc.getInventoryNumber() < 0) {
            return lang.negativeInventoryNumber();
        }
        return null;
    }

    @Inject
    Lang lang;
    @Inject
    AbstractEquipmentDocumentEditView view;
    @Inject
    EquipmentControllerAsync equipmentController;
    @Inject
    DefaultErrorHandler errorHandler;

    private Document document;
    private List<String> decimalNumbers;
    private Profile authorizedProfile;
    private AppEvents.InitDetails initDetails;
}
