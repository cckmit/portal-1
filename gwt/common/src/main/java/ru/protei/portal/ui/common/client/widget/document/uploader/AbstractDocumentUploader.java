package ru.protei.portal.ui.common.client.widget.document.uploader;

import com.google.gwt.event.dom.client.HasChangeHandlers;
import ru.protei.portal.core.model.dict.En_DocumentFormat;
import ru.protei.portal.core.model.ent.Document;

public interface AbstractDocumentUploader extends HasChangeHandlers {

    String getFilename();

    boolean isFileSet();

    void resetForm();

    void submitForm(String url);

    void resetAction();

    void uploadBindToDocument(Document document);

    void setUploadHandler(UploadHandler uploadHandler);

    void setResetHandler(ResetHandler resetHandler);

    void setFormat(En_DocumentFormat format);

    boolean isValidFileFormat();

    interface ResetHandler {
        void onFormReset();
    }
}
