package ru.protei.portal.ui.common.client.widget.uploader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.FormPanel;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.Attachment;

import java.util.Date;

/**
 * Created by bondarenko on 03.07.17.
 */
public class AttachmentUploader extends FileUploader{

    public interface FileUploadHandler{
        void onSuccess(Attachment attachment);
        void onError();
        enum TYPE {TEXT, BINARY, DATAURL};

        // check the filename and extension and return true if you are happy with proceeding
        // returnning false will prevent the file from being read
        default boolean checkFileName(String fileName) {
            return false;
        }

        // tell the method to use to read this file
        default TYPE specifyFileType() {
            return null;
        }

        // do your stuff here, eg upload to a server
        default void handleFileContent(String fileName, String fileContent) {}
    }

    public AttachmentUploader() {
        addDomHandler(event -> {
//            overlay(true);
        }, DragEnterEvent.getType());

        addDomHandler(event -> {
//            overlay(false);
        }, DragLeaveEvent.getType());

        addDomHandler(event -> {
            // stop default behaviour
            event.preventDefault();
            event.stopPropagation();
            // starts the fetching, reading and callbacks
            if(uploadHandler != null) {
                handleFiles(event.getDataTransfer(), uploadHandler);
            }

//            overlay(false)
        }, DropEvent.getType());
    }

    @Override
    public void submitCompleteHandler(FormPanel.SubmitCompleteEvent event) {
        resetForm();
        onUploaded(event.getResults());
    }

    @Override
    public void changeHandler(ChangeEvent event) {
        String filename = fileUpload.getFilename();
        if (filename.length() != 0 && !form.getElement().hasClassName("attachment-uploading")) {
            form.addStyleName("attachment-uploading");
            if(caseNumber != null){
                form.setAction(UPLOAD_WITH_AUTOBINDING_URL + "/" + caseType.getId() + "/" + caseNumber);
            }else
                form.setAction(UPLOAD_WITHOUT_AUTOBINDING_URL);
            form.submit();
            fileUpload.setEnabled(false);
        }
    }

    public void uploadBase64File(String json) {
        try {
            if (!fileUpload.isEnabled()) {
                return;
            }
            form.addStyleName("attachment-uploading");
            fileUpload.setEnabled(false);
            RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL.encode(UPLOAD_BASE_64_FILE_URL));
            builder.setHeader("Content-type", "application/json");
            builder.sendRequest(json, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    resetForm();
                    onUploaded(response.getText());
                }
                @Override
                public void onError(Request request, Throwable exception) {
                    resetForm();
                }
            });
        } catch (RequestException e) {
            resetForm();
        }
    }

    /**
     * При успешной загрузке файла автоматически делает связку с кейсом
     * @param caseNumber номер кейса
     */
    public void autoBindingToCase(En_CaseType caseType, Long caseNumber){
        this.caseType = caseType;
        this.caseNumber = caseNumber;
    }

    public void setUploadHandler(FileUploadHandler fileUploadHandler){
        this.uploadHandler = fileUploadHandler;
    }

    private void resetForm() {
        form.removeStyleName("attachment-uploading");
        form.reset();
        fileUpload.setEnabled(true);
    }

    private void onUploaded(String response) {
        if (uploadHandler == null) {
            return;
        }
        Attachment attachment = createAttachment(response);
        if (attachment == null) {
            uploadHandler.onError();
        } else {
            uploadHandler.onSuccess(attachment);
        }
    }

    private Attachment createAttachment(String json){
        if(json == null || json.isEmpty() || json.equals("error"))
            return null;

        JSONObject jsonObj = JSONParser.parseStrict(json).isObject();

        Attachment attachment = new Attachment();
        attachment.setId(Long.valueOf(jsonObj.get("id").toString()));
        attachment.setFileName(jsonObj.get("fileName").isString().stringValue());
        attachment.setCreatorId(Long.valueOf(jsonObj.get("creatorId").toString()));
        attachment.setExtLink(jsonObj.get("extLink").isString().stringValue());
        attachment.setDataSize(Long.valueOf(jsonObj.get("dataSize").toString()));
        attachment.setMimeType(jsonObj.get("mimeType").isString().stringValue());
        attachment.setCreated(new Date((long)jsonObj.get("created").isNumber().doubleValue()));
        return attachment;
    }

    private final native void handleFiles(JavaScriptObject dataTransfer, FileUploadHandler fileUploadHandler) /*-{
        var files = dataTransfer.files;
        var i;
        var file;
        var reader = new FileReader();
        for(i = 0; i < files.length; i++) {
            file = files[i];
            if(fileUploadHandler.@ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader.FileUploadHandler::checkFileName(Ljava/lang/String;)(file.name)) {
                var type = fileUploadHandler.@ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader.FileUploadHandler::specifyFileType()();
                reader.onload = function(e) {
                    fileUploadHandler.@ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader.FileUploadHandler::handleFileContent(Ljava/lang/String;Ljava/lang/String;)(file.name, e.target.result);
                }
                if(type == "TEXT") {
                    reader.readAsText(file);
                } else if(type == "BINARY") {
                    reader.readAsBinaryString(file);
                } else if(type == "DATAURL") {
                    reader.readAsDataURL(file);
                    // not supported
//    } else if(type == "ARRAYBUFFER") {
//     reader.readAsArrayBuffer(file);
                } else {
                }
            }
        }
    }-*/;

    private static final String UPLOAD_WITHOUT_AUTOBINDING_URL = GWT.getModuleBaseURL() + "springApi/uploadFile";
    private static final String UPLOAD_WITH_AUTOBINDING_URL = GWT.getModuleBaseURL() + "springApi/uploadFileToCase";
    private static final String UPLOAD_BASE_64_FILE_URL = GWT.getModuleBaseURL() + "springApi/uploadBase64File";
    private FileUploadHandler uploadHandler;
    private En_CaseType caseType;
    private Long caseNumber;
}
