package ru.protei.portal.ui.document.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DecimalNumberEntityType;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.DocumentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DocumentControllerAsync;
import ru.protei.portal.ui.common.client.service.EquipmentControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.document.client.widget.uploader.UploadHandler;

public abstract class DocumentEditActivity
        implements Activity, AbstractDocumentEditActivity {

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
                fireEvent(new DocumentEvents.ChangeModel());
                fireEvent(new Back());
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

    private void fireErrorMessage(String msg) {
        fireEvent(new NotifyEvents.Show(msg, NotifyEvents.NotifyType.ERROR));
    }

    @Override
    public void onSaveClicked() {
        Document newDocument = getDocument();
        DecimalNumber newDecimalNumber = newDocument.getDecimalNumber();
        boolean decimalNumberEmpty = newDecimalNumber == null || newDecimalNumber.isCompletelyEmpty();

        if (newDocument.getId() == null && HelperFunc.isEmpty(view.documentUploader().getFilename())) {
            fireErrorMessage(lang.uploadingDocumentNotSet());
            return;
        }
        if (!newDocument.isValid()) {
            fireErrorMessage(getValidationErrorMessage(newDocument));
            return;
        } else if (!decimalNumberEmpty && !newDecimalNumber.isValid()) {
            view.decimalNumberValidator().setValid(false);
            return;
        }

        boolean decimalNumberWasSet = newDecimalNumber != null && newDecimalNumber.getId() != null;
        if (decimalNumberWasSet && decimalNumberEmpty) {
            fireErrorMessage(lang.decimalNumberNotSet());
            return;
        }

        if (decimalNumberEmpty) {
            saveDocument(newDocument);
            return;
        }

        equipmentService.findDecimalNumber(newDocument.getDecimalNumber(), new RequestCallback<DecimalNumber>() {
            @Override
            public void onError(Throwable throwable) {
                fireErrorMessage(lang.decimalNumberNotFound());
            }

            @Override
            public void onSuccess(DecimalNumber decimalNumber) {
                newDocument.setDecimalNumber(decimalNumber);
                saveDocument(newDocument);
            }
        });
    }

    private void saveDocument(Document document) {
        boolean isNew = document.getId() == null;
        documentService.saveDocument(document, new RequestCallback<Document>() {
            @Override
            public void onError(Throwable throwable) {
                fireErrorMessage(lang.errDocumentNotSaved());
            }

            @Override
            public void onSuccess(Document result) {
                if (isNew) {
                    view.documentUploader().uploadBindToDocument(result);
                } else {
                    fireEvent(new Back());
                }
            }
        });
    }

    private String getValidationErrorMessage(Document doc) {
        if (doc.getDecimalNumber() == null) {
            return lang.decimalNumberNotSet();
        }
        if (doc.getType() == null) {
            return lang.documentTypeIsEmpty();
        }
        if (doc.getProjectId() == null) {
            return lang.documentProjectIsEmpty();
        }
        if (doc.getManagerId() == null) {
            return lang.customerNotSet();
        }
        if (doc.getInventoryNumber() == null) {
            return lang.inventoryNumberIsEmpty();
        }
        if (doc.getInventoryNumber() <= 0) {
            return lang.negativeInventoryNumber();
        }
        if (HelperFunc.isEmpty(doc.getName())) {
            return lang.documentNameIsNotSet();
        }
        return null;
    }


    private Document getDocument() {
        Document d = new Document();
        d.setId(document.getId());
        d.setName(view.name().getValue());
        d.setAnnotation(view.annotation().getValue());
        DecimalNumber decimalNumber = view.decimalNumber().getValue();
        if (decimalNumber != null)
            decimalNumber.setEntityType(En_DecimalNumberEntityType.DOCUMENT);
        d.setDecimalNumber(decimalNumber);
        d.setType(view.documentType().getValue());
        d.setTypeCode(view.typeCode().getValue());
        d.setInventoryNumber(view.inventoryNumber().getValue());
        d.setKeywords(view.keywords().getValue());
        d.setManagerId(view.manager().getValue() == null ? null : view.manager().getValue().getId());
        d.setProjectId(view.project().getValue().getId());
        return d;
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    private void fillView(Document document) {
        this.document = document;

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        PersonShortView manager = new PersonShortView();
        manager.setId(document.getManagerId());

        view.name().setValue(document.getName());
        view.annotation().setValue(document.getAnnotation());
        view.created().setValue(DateFormatter.formatDateTime(document.getCreated()));
        view.typeCode().setValue(document.getTypeCode());
        view.decimalNumber().setValue(document.getDecimalNumber());
        view.documentCategory().setValue(document.getType() == null ? null : document.getType().getDocumentCategory(), true);
        view.documentType().setValue(document.getType(), true);
        view.inventoryNumber().setValue(document.getInventoryNumber());
        view.keywords().setValue(document.getKeywords());
        view.manager().setValue(manager);
        view.project().setValue(document.getProjectInfo());

        view.setEnabledProject(document.getId() == null);
        view.setVisibleUploader(document.getId() == null);

        view.nameValidator().setValid(true);
        view.decimalNumberValidator().setValid(true);

        view.documentUploader().resetFilename();
    }

    @Inject
    AbstractDocumentEditView view;

    @Inject
    Lang lang;

    Document document;

    @Inject
    DocumentControllerAsync documentService;

    @Inject
    EquipmentControllerAsync equipmentService;

    private AppEvents.InitDetails initDetails;
}
