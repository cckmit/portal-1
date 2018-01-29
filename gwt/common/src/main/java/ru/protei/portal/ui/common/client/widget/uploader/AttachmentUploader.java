package ru.protei.portal.ui.common.client.widget.uploader;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.FormPanel;
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
        fileUpload.setEnabled(true);
        if(uploadHandler == null)
            return;

        Attachment attachment = createAttachment(event.getResults());
        if(attachment == null)
            uploadHandler.onError();
        else
            uploadHandler.onSuccess(attachment);
    }

    @Override
    public void changeHandler(ChangeEvent event) {
        String filename = fileUpload.getFilename();
        if (filename.length() != 0 && !form.getElement().hasClassName("attachment-uploading")) {
            form.addStyleName("attachment-uploading");
            if(caseId != null){
                form.setAction(UPLOAD_WITH_AUTOBINDING_URL + caseId);
            }else
                form.setAction(UPLOAD_WITHOUT_AUTOBINDING_URL);
            form.submit();
            fileUpload.setEnabled(false);
        }
    }

    /**
     * При успешной загрузке файла автоматически делает связку с кейсом
     * @param caseId id кейса
     */
    public void autoBindingToCase(Long caseId){
        this.caseId = caseId;
    }

    public void setUploadHandler(FileUploadHandler fileUploadHandler){
        this.uploadHandler = fileUploadHandler;
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

    private static final String UPLOAD_WITHOUT_AUTOBINDING_URL = "Crm/springApi/uploadFile";
    private static final String UPLOAD_WITH_AUTOBINDING_URL = "Crm/springApi/uploadFileToCase";
    private FileUploadHandler uploadHandler;
    private Long caseId;
}
