package ru.protei.portal.ui.common.client.widget.document.uploader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import ru.protei.portal.core.model.dict.En_DocumentFormat;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.ui.common.client.util.FilenameUtils;
import ru.protei.portal.ui.common.client.widget.uploaderdropzone.FileDropzoneUploader;

import java.util.Objects;

public class DocumentUploader extends FileDropzoneUploader implements AbstractDocumentUploader, ru.protei.portal.ui.common.client.widget.uploaderdropzone.UploadHandler {

    public DocumentUploader() {
        setUploadHandler(this);
    }

    @Override
    public void setFormat(En_DocumentFormat format) {
        this.format = format;
        setAccept(makeMimeTypes(format));
    }

    @Override
    public boolean isValidFileFormat() {
        if (format == null) {
            return false;
        }
        if (HelperFunc.isEmpty(getFilename())) {
            return false;
        }
        String fileExtension = FilenameUtils.getExtension(getFilename());
        if (format == En_DocumentFormat.DOC || format == En_DocumentFormat.DOCX) {
            return Objects.equals(En_DocumentFormat.DOC.getExtension(), fileExtension) ||
                    Objects.equals(En_DocumentFormat.DOCX.getExtension(), fileExtension);
        }
        return Objects.equals(format.getExtension(), fileExtension);
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
    public boolean isFileSet() {
        return HelperFunc.isNotEmpty(getFilename());
    }

    @Override
    public void submitForm(String url) {
        super.submitForm(url + format.name().toLowerCase());
    }

    @Override
    public void resetForm() {
        super.resetForm();
        if (resetHandler != null) {
            resetHandler.onFormReset();
        }
    }

    @Override
    public void resetAction() {
        form.setAction("javascript:void(0);");
    }

    @Override
    public void onChange() {}

    @Override
    public void onComplete(String result) {
        resetForm();
        if (uploadHandler == null) return;
        if ("error".equals(result)) {
            uploadHandler.onError();
        } else {
            uploadHandler.onSuccess();
        }
    }

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
        if (format == null) {
            return "";
        }
        if (format == En_DocumentFormat.DOC || format == En_DocumentFormat.DOCX) {
            return En_DocumentFormat.DOC.getMimeType() + "," + En_DocumentFormat.DOCX.getMimeType();
        }
        return format.getMimeType();
    }

    private ResetHandler resetHandler;
    private UploadHandler uploadHandler;
    private En_DocumentFormat format;
    private static final String UPLOAD_DOCUMENT_URL = GWT.getModuleBaseURL() + "springApi/upload/document/";
}
