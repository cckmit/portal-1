package ru.protei.portal.ui.common.client.widget.uploader.impl.buttonpanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.ui.common.client.widget.uploader.AbstractAttachmentUploader;
import ru.protei.portal.ui.common.client.widget.uploader.impl.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.uploader.impl.PasteInfo;

import java.util.List;

public class ButtonPanelAttachmentUploader extends Composite implements AbstractAttachmentUploader {
    public ButtonPanelAttachmentUploader() {
        initWidget(ourUiBinder.createAndBindUi(this));
        addDomHandler(event -> attachmentUploader.initUploading(), ClickEvent.getType());
    }

    @Override
    public void setUploadHandler(AttachmentUploader.FileUploadHandler handler) {
        attachmentUploader.setUploadHandler(handler);
    }

    @Override
    public void autoBindingToCase(En_CaseType caseType, Long caseNumber) {
        attachmentUploader.autoBindingToCase(caseType, caseNumber);
    }

    @Override
    public void uploadBase64File(String json, PasteInfo pasteInfo) {
        attachmentUploader.uploadBase64File(json, pasteInfo);
    }

    @Override
    public void uploadBase64Files(List<String> jsons, PasteInfo pasteInfo) {
        attachmentUploader.uploadBase64Files(jsons, pasteInfo);
    }

    public void setEnsureDebugId(String debugId) {
        attachmentUploader.setEnsureDebugId(debugId);
    }

    @UiField
    AttachmentUploader attachmentUploader;

    interface ButtonPanelAttachmentUploaderUiBinder extends UiBinder<HTMLPanel, ButtonPanelAttachmentUploader> {}
    private static ButtonPanelAttachmentUploaderUiBinder ourUiBinder = GWT.create(ButtonPanelAttachmentUploaderUiBinder.class);
}
