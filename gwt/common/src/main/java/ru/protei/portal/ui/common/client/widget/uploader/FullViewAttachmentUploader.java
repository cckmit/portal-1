package ru.protei.portal.ui.common.client.widget.uploader;

import com.google.gwt.event.dom.client.ClickEvent;

public class FullViewAttachmentUploader extends AttachmentUploader {
    public FullViewAttachmentUploader() {
        addDomHandler(event -> initUploading(), ClickEvent.getType());
    }
}
