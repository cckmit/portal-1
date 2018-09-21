package ru.protei.portal.ui.equipment.client.activity.document.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EquipmentControllerAsync;
import ru.protei.portal.ui.common.client.widget.document.uploader.UploadHandler;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.DefaultNotificationHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;

public abstract class EquipmentDocumentEditActivity implements Activity, AbstractEquipmentDocumentEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.documentUploader().setUploadHandler(new UploadHandler() {
            @Override
            public void onError() {
                notificationHandler.accept(lang.errSaveDocumentFile(), NotifyEvents.NotifyType.ERROR);
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

        decimalNumber = null;

        if (event.documentId == null) {
            if (event.projectId == null || StringUtils.isBlank(event.decimalNumber)) {
                notificationHandler.accept(lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR);
                fireEvent(new Back());
                return;
            }
            drawView();
            fireEvent(new AppEvents.InitPanelName(lang.documentCreate()));
            decimalNumber = event.decimalNumber;
            Document document = new Document();
            document.setApproved(false);
            document.setProjectId(event.projectId);
            document.setDecimalNumber(event.decimalNumber);
            fillView(document);
        } else {
            drawView();
            fireEvent(new AppEvents.InitPanelName(lang.documentEdit()));
            equipmentController.getDocument(event.documentId, new FluentCallback<Document>()
                    .withError(throwable -> {
                        errorHandler.accept(throwable);
                        fireEvent(new Back());
                    })
                    .withSuccess(this::fillView)
            );
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
        view.approved().setValue(document.getApproved());
        view.approvedEnabled().setEnabled(approveMode);
        view.setDocumentCategory(En_DocumentCategory.TD);
        view.documentCategoryEnabled().setEnabled(false);
        view.documentType().setValue(document.getType());
        view.version().setValue(document.getVersion());
        view.decimalNumber().setValue(document.getDecimalNumber());
        view.decimalNumberEnabled().setEnabled(false);
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
        document.setName(view.name().getValue());
        document.setApproved(view.approved().getValue());
        document.setType(view.documentType().getValue());
        document.setVersion(view.version().getValue());
        document.setInventoryNumber(view.approved().getValue() ? view.inventoryNumber().getValue() : null);
        document.setContractor(Person.fromPersonShortView(view.contractor().getValue()));
        document.setRegistrar(Person.fromPersonShortView(view.registrar().getValue()));
        document.setAnnotation(view.annotation().getValue());
        document.setKeywords(view.keywords().getValue());
        if (document.getId() == null && decimalNumber != null) {
            document.setDecimalNumber(decimalNumber + "-" + view.documentType().getValue().getShortName());
        }
    }

    private void saveDocument() {
        view.saveButtonEnabled().setEnabled(false);
        view.cancelButtonEnabled().setEnabled(false);
        if ((document.getId() == null || !document.getApproved()) && StringUtils.isNotBlank(view.documentUploader().getFilename())) {
            notificationHandler.accept(lang.documentSaving(), NotifyEvents.NotifyType.INFO);
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
                            notificationHandler.accept(lang.equipmentDocumentAlreadyExists(), NotifyEvents.NotifyType.ERROR);
                            return;
                        }
                    }
                    errorHandler.accept(throwable);
                })
                .withSuccess(doc -> {
                    notificationHandler.accept(lang.documentSaved(), NotifyEvents.NotifyType.SUCCESS);
                    fireEvent(new DocumentEvents.ChangeModel());
                    fireEvent(new Back());
                })
        );
    }

    private boolean checkDocumentUploadValid(Document document) {
        if (document.getId() == null && StringUtils.isBlank(view.documentUploader().getFilename())) {
            notificationHandler.accept(lang.uploadingDocumentNotSet(), NotifyEvents.NotifyType.ERROR);
            return false;
        }
        return true;
    }

    private boolean checkDocumentValid(Document document) {
        if (!document.isValid()) {
            notificationHandler.accept(getValidationErrorMessage(document), NotifyEvents.NotifyType.ERROR);
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
    @Inject
    DefaultNotificationHandler notificationHandler;

    private Document document;
    private String decimalNumber;
    private Profile authorizedProfile;
    private AppEvents.InitDetails initDetails;
}
