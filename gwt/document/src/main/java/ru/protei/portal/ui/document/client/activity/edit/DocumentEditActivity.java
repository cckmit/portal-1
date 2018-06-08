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
import ru.protei.portal.ui.common.client.service.DocumentServiceAsync;
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
        boolean isNew = document.getId() == null;

        Document document = applyChanges();
        if (!document.isValid() || isUploadingDocumentNotValid(document)) {
            fireErrorMessage(getValidationErrorMessage(document));
            return;
        } else if (!view.isDecimalNumbersCorrect()) {
            return;
        }

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

    private boolean isUploadingDocumentNotValid(Document doc) {
        return HelperFunc.isEmpty(view.documentUploader().getFilename()) && doc.getId() == null;
    }

    private String getValidationErrorMessage(Document doc) {
        if (isUploadingDocumentNotValid(doc)) {
            return lang.uploadingDocumentNotSet();
        }
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


    private Document applyChanges() {
        document.setName(view.name().getValue());
        document.setAnnotation(view.annotation().getValue());
        DecimalNumber decimalNumber = view.decimalNumber().getValue();
        if (decimalNumber != null) {
            decimalNumber.setEntityType(En_DecimalNumberEntityType.DOCUMENT);
        }
        document.setDecimalNumber(decimalNumber);
        document.setType(view.documentType().getValue());
        document.setInventoryNumber(view.inventoryNumber().getValue());
        document.setKeywords(view.keywords().getValue());
        document.setManagerId(view.manager().getValue() == null ? null : view.manager().getValue().getId());
        document.setProjectId(view.project().getValue().getId());
        return document;
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
    }

    @Inject
    AbstractDocumentEditView view;

    @Inject
    Lang lang;

    Document document;

    @Inject
    DocumentServiceAsync documentService;

    private AppEvents.InitDetails initDetails;
}
