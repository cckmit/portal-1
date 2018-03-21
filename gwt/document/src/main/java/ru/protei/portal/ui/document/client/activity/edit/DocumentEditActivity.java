package ru.protei.portal.ui.document.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AttachmentEvents;
import ru.protei.portal.ui.common.client.events.DocumentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DocumentServiceAsync;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static ru.protei.portal.core.model.helper.DocumentHelper.isDocumentValid;

public abstract class DocumentEditActivity
        implements Activity, AbstractDocumentEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.setFileUploadHandler(new AttachmentUploader.FileUploadHandler() {
            @Override
            public void onSuccess(Attachment attachment) {
                addAttachmentsToCase(Collections.singleton(attachment));
            }

            @Override
            public void onError() {
                fireEvent(new NotifyEvents.Show(lang.uploadFileError(), NotifyEvents.NotifyType.ERROR));
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
                fireErrorMessage(lang.errGetList());
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
        Document document = applyChanges();
        if (!isDocumentValid(document)) {
            fireEvent(new NotifyEvents.Show(getValidationErrorMessage(document), NotifyEvents.NotifyType.ERROR));
            return;
        } else if (!view.isDecimalNumbersCorrect()) {
            return;
        }

        documentService.saveDocument(document, new RequestCallback<Document>() {
            @Override
            public void onError(Throwable throwable) {
                fireErrorMessage(throwable.getMessage());
            }

            @Override
            public void onSuccess(Document result) {
                fireEvent(new DocumentEvents.ChangeModel());
                fireEvent(new Back());
            }
        });
    }

    @Event
    public void onAddAttachments(AttachmentEvents.Add event) {
        if (view.isAttached() && document.getId().equals(event.caseId)) {
            addAttachmentsToCase(event.attachments);
        }
    }

    @Event
    public void onRemoveAttachments(AttachmentEvents.Remove event) {
        if (view.isAttached() && document.getId().equals(event.caseId)) {
            event.attachments.forEach(view.attachmentsContainer()::remove);
            attachments.removeAll(event.attachments);
        }
    }

    private String getValidationErrorMessage(Document doc) {
        if (isDocumentValid(doc)) {
            return null;
        }
        if (doc.getDecimalNumber() == null) {
            return lang.decimalNumberNotSet();
        }
        if (doc.getType() == null) {
            return lang.documentTypeIsEmpty();
        }
        if (HelperFunc.isEmpty(doc.getProject())) {
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
        document.setDecimalNumber(view.decimalNumber().getValue());
        document.setType(view.documentType().getValue());
        document.setInventoryNumber(view.inventoryNumber().getValue());
        document.setKeywords(view.keywords().getValue());
        document.setManagerId(view.manager().getValue() == null ? null : view.manager().getValue().getId());
        document.setProject(view.project().getValue());
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
        view.documentType().setValue(document.getType());
        view.inventoryNumber().setValue(document.getInventoryNumber());
        view.keywords().setValue(document.getKeywords());
        view.manager().setValue(manager);
        view.project().setValue(document.getProject());

        view.attachmentsContainer().clear();
        view.setCaseId(document.getId());
        if (document.getId() != null) {
            view.attachmentsContainer().add(attachments);
        }
    }

    private void addAttachmentsToCase(Collection<Attachment> attachments) {
        view.attachmentsContainer().add(attachments);
        if (attachments == null) {
            attachments = new LinkedList<>();
        }
        attachments.addAll(attachments);
    }

    @Inject
    AbstractDocumentEditView view;

    @Inject
    Lang lang;

    Document document;

    List<Attachment> attachments = new LinkedList<>();

    @Inject
    DocumentServiceAsync documentService;

    private AppEvents.InitDetails initDetails;
}
