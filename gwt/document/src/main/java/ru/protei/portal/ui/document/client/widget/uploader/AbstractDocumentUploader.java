package ru.protei.portal.ui.document.client.widget.uploader;

import ru.protei.portal.core.model.ent.Document;

public interface AbstractDocumentUploader {
    String getFilename();

    void resetAction();

    void uploadBindToDocument(Document document);

    void setUploadHandler(UploadHandler uploadHandler);
}
