package ru.protei.portal.ui.document.client.widget.uploader;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FormPanel;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.ui.common.client.widget.uploader.FileUploader;

public class DocumentUploader extends FileUploader {

    public DocumentUploader() {
        fileUpload.getElement().setAttribute("accept", "application/pdf");
    }

    public void click() {
        fileUpload.click();
    }

    public void addChangeHandler(ChangeHandler changeHandler) {
        fileUpload.addChangeHandler(changeHandler);
    }


    public String getFilename() {
        return fileUpload.getFilename();
    }

    @Override
    public void submitCompleteHandler(FormPanel.SubmitCompleteEvent event) {
        form.removeStyleName("attachment-uploading");
        fileUpload.setEnabled(true);
        if (uploadHandler == null)
            return;
        if ("error".equals(event.getResults())) {
            uploadHandler.onError();
        } else {
            uploadHandler.onSuccess();
        }
    }

    @Override
    public void changeHandler(ChangeEvent event) {
    }

    public void uploadBindToDocument(Document document) {
        if (HelperFunc.isEmpty(getFilename()) || form.getElement().hasClassName("attachment-uploading")) {
            return;
        }

        Long documentId = document.getId();
        Long projectId = document.getProjectId();
        String url = UPLOAD_DOCUMENT_URL + projectId + "/" + documentId;

        form.addStyleName("attachment-uploading");
        form.setAction(url);
        form.submit();
        fileUpload.setEnabled(false);
    }

    public void setUploadHandler(UploadHandler uploadHandler) {
        this.uploadHandler = uploadHandler;
    }

    private UploadHandler uploadHandler;
    private static final String UPLOAD_DOCUMENT_URL = "Crm/springApi/uploadDocument/";

    public interface UploadHandler {

        void onError();

        void onSuccess();
    }
}
