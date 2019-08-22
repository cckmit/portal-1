package ru.protei.portal.ui.common.client.widget.uploader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.FormPanel;
import ru.protei.portal.core.model.struct.UploadResult;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_FileUploadStatus;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.util.JsonUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by bondarenko on 03.07.17.
 */
public class AttachmentUploader extends FileUploader{

    public interface FileUploadHandler{
        void onSuccess(Attachment attachment);
        void onError(En_FileUploadStatus status, String details);
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
        sendJsonRequest(json, UPLOAD_BASE_64_FILE_URL);
    }

    public void uploadBase64Files(List<String> jsons) {
        sendJsonRequest(JsonUtils.wrapJsonsToJsonList(jsons), UPLOAD_BASE_64_FILES_URL);
    }

    private void sendJsonRequest(String json, String url) {
        try {
            if (!fileUpload.isEnabled()) {
                return;
            }
            form.addStyleName("attachment-uploading");
            fileUpload.setEnabled(false);
            RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL.encode(url));
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

        UploadResult result = parseUploadResult(response);

        if (!En_FileUploadStatus.OK.equals(result.getStatus())) {
            uploadHandler.onError(result.getStatus(), result.getDetails());
        } else {
            parseAttachmentDispatcher(result.getDetails());
        }
    }

    private UploadResult parseUploadResult(String json){
        UploadResult result;

        if (json == null || json.isEmpty()) {
            result = new UploadResult(En_FileUploadStatus.PARSE_ERROR, "");
        } else {
            result = new UploadResult();
            try {
                JSONObject jsonObj = JSONParser.parseStrict(json).isObject();
                result.setStatus(En_FileUploadStatus.getStatus(jsonObj.get("status").isString().stringValue()));
                result.setDetails(jsonObj.get("details").isString().stringValue());
            } catch (Exception e){
                result.setStatus(En_FileUploadStatus.PARSE_ERROR);
                result.setDetails(json);
            }
        }

        return result;
    }

    private void parseAttachmentDispatcher(String json) {
        JSONObject jsonObj = JSONParser.parseStrict(json).isObject();

        if (jsonObj != null) {
            parseAttachment(jsonObj);
        } else {
            parseAttachments(JSONParser.parseStrict(json).isArray());
        }
    }

    private void parseAttachment(JSONObject jsonObj){
        Attachment attachment = new Attachment();
        attachment.setId(Long.valueOf(jsonObj.get("id").toString()));
        attachment.setFileName(jsonObj.get("fileName").isString().stringValue());
        attachment.setCreatorId(Long.valueOf(jsonObj.get("creatorId").toString()));
        attachment.setExtLink(jsonObj.get("extLink").isString().stringValue());
        attachment.setDataSize(Long.valueOf(jsonObj.get("dataSize").toString()));
        attachment.setMimeType(jsonObj.get("mimeType").isString().stringValue());
        attachment.setCreated(new Date((long)jsonObj.get("created").isNumber().doubleValue()));

        uploadHandler.onSuccess(attachment);
    }

    private void parseAttachments(JSONArray array) {
        for (int i = 0; i < array.size(); i++) {
            parseAttachment(array.get(i).isObject());
        }
    }


    private static final String UPLOAD_WITHOUT_AUTOBINDING_URL = GWT.getModuleBaseURL() + "springApi/uploadFile";
    private static final String UPLOAD_WITH_AUTOBINDING_URL = GWT.getModuleBaseURL() + "springApi/uploadFileToCase";
    private static final String UPLOAD_BASE_64_FILES_URL = GWT.getModuleBaseURL() + "springApi/uploadBase64Files";
    private static final String UPLOAD_BASE_64_FILE_URL = GWT.getModuleBaseURL() + "springApi/uploadBase64File";
    private FileUploadHandler uploadHandler;
    private En_CaseType caseType;
    private Long caseNumber;
}
