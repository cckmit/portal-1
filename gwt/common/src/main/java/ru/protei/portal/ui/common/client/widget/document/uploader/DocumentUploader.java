package ru.protei.portal.ui.common.client.widget.document.uploader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FormPanel;
import ru.protei.portal.core.model.dict.En_DocumentFormat;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.ui.common.client.widget.uploader.FileUploader;

public class DocumentUploader extends FileUploader implements AbstractDocumentUploader {

    public void click() {
        fileUpload.click();
    }

    public void setFormat(En_DocumentFormat format) {
        this.format = format;
        fileUpload.getElement().setAttribute("accept", makeMimeTypes(this.format));
    }

    @Override
    public HandlerRegistration addChangeHandler(ChangeHandler changeHandler) {
        return fileUpload.addChangeHandler(changeHandler);
    }

    @Override
    public String getFilename() {
        return fileUpload.getFilename();
    }

    @Override
    public void submitForm(String url) {
        form.setAction(url + format.getFormat());
        form.addStyleName("attachment-uploading");
        form.submit();
        fileUpload.setEnabled(false);
    }

    @Override
    public void resetForm() {
        form.removeStyleName("attachment-uploading");
        form.reset();
        fileUpload.setEnabled(true);
        if (resetHandler != null) {
            resetHandler.onFormReset();
        }
    }

    @Override
    public void resetAction() {
        form.setAction("javascript:void(0);");
    }

    @Override
    public void submitCompleteHandler(FormPanel.SubmitCompleteEvent event) {
        resetForm();
        if (uploadHandler == null)
            return;
        if ("error".equals(event.getResults())) {
            uploadHandler.onError();
        } else {
            uploadHandler.onSuccess();
        }
    }

    @Override
    public void changeHandler(ChangeEvent event) {}

    @Override
    public void uploadBindToDocument(Document document) {
        if (format == null) {
            return;
        }
        if (HelperFunc.isEmpty(getFilename())) {
            return;
        }
        if (form.getElement().hasClassName("attachment-uploading")) {
            return;
        }
        submitForm(UPLOAD_DOCUMENT_URL);
    }

    @Override
    public void setUploadHandler(UploadHandler uploadHandler) {
        this.uploadHandler = uploadHandler;
    }

    @Override
    public void setResetHandler(ResetHandler resetHandler) {
        this.resetHandler = resetHandler;
    }

    private String makeMimeTypes(En_DocumentFormat format) {
        if (format == En_DocumentFormat.PDF) {
            return En_DocumentFormat.PDF.getMimeType();
        }
        return En_DocumentFormat.DOCX.getMimeType() + "," + En_DocumentFormat.DOC.getMimeType();
    }

    private ResetHandler resetHandler;
    private UploadHandler uploadHandler;
    private En_DocumentFormat format;
    private static final String UPLOAD_DOCUMENT_URL = GWT.getModuleBaseURL() + "springApi/upload/document/";
}
