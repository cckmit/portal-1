package ru.protei.portal.ui.document.client.widget.uploader;

import com.google.gwt.event.dom.client.HasChangeHandlers;
import ru.protei.portal.core.model.ent.Document;

public interface AbstractDocumentUploader extends HasChangeHandlers {
    String getFilename();

    void resetAction();

    void uploadBindToDocument(Document document);

    void setUploadHandler(UploadHandler uploadHandler);
}
