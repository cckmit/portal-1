package ru.protei.portal.ui.common.client.widget.uploader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
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
    }

    @Override
    public void submitCompleteHandler(FormPanel.SubmitCompleteEvent event) {
        form.removeStyleName("attachment-uploading");
        form.reset();
        fileUpload.setEnabled(true);
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
            RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL.encode(UPLOAD_BASE_64_FILE_URL));
            builder.setHeader("Content-type", "application/json");
            builder.sendRequest(json, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    onUploaded(response.getText());
                }
                @Override
                public void onError(Request request, Throwable exception) {}
            });
        } catch (RequestException e) {}
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

    private static final String UPLOAD_WITHOUT_AUTOBINDING_URL = GWT.getModuleBaseURL() + "springApi/uploadFile";
    private static final String UPLOAD_WITH_AUTOBINDING_URL = GWT.getModuleBaseURL() + "springApi/uploadFileToCase";
    private static final String UPLOAD_BASE_64_FILE_URL = GWT.getModuleBaseURL() + "springApi/uploadBase64File";
    private FileUploadHandler uploadHandler;
    private En_CaseType caseType;
    private Long caseNumber;
}
