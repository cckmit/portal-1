package ru.protei.portal.ui.common.client.widget.uploader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.annotations.IsSafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.ent.Attachment;

/**
 * Загрузчик файлов
 */
public class FileUploader extends Composite implements HasHTML, HasSafeHtml {

    public interface FileUploadHandler{
        void onSuccess(Attachment attachment);
        void onError();
    }

    public FileUploader() {
        this((String) null);
    }

    public FileUploader(SafeHtml html){
        this(html.asString());
    }

    public FileUploader(@IsSafeHtml String html){
        initWidget(ourUiBinder.createAndBindUi(this));

        if(html != null && !html.isEmpty())
            setHTML(html);

        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);

        form.addSubmitCompleteHandler( event -> {
                if(fileUploadHandler == null)
                    return;

                Attachment attachment = createAttachment(event.getResults());
                if(attachment == null)
                    fileUploadHandler.onError();
                else
                    fileUploadHandler.onSuccess(attachment);
        });

        fileUpload.addChangeHandler( event -> {
                String filename = fileUpload.getFilename();
                if (filename.length() != 0) {
                    if(caseId != null){
                        form.setAction(UPLOAD_WITH_AUTOBINDING_URL + caseId);
                    }else
                        form.setAction(UPLOAD_WITHOUT_AUTOBINDING_URL);
                    form.submit();
                }
        });
    }

    @Override
    public void setHTML(SafeHtml html) {
        setHTML(html.asString());
    }

    @Override
    public String getHTML() {
        return visibleContent.getElement().getInnerHTML();
    }

    @Override
    public void setHTML(@IsSafeHtml String html) {
        visibleContent.getElement().setInnerHTML(html);
    }

    @Override
    public String getText() {
        return visibleContent.getElement().getInnerText();
    }

    @Override
    public void setText(String text) {
        visibleContent.getElement().setInnerText(text);
    }

    /**
     * При успешной загрузке файла автоматически делает связку с кейсом
     * @param caseId id кейса
     */
    public void autoBindingFilesToCase(Long caseId){
        this.caseId = caseId;
    }

    public void setFileUploadHandler(FileUploadHandler fileUploadHandler){
        this.fileUploadHandler = fileUploadHandler;
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
        return attachment;
    }


    @UiField
    FormPanel form;
    @UiField
    FileUpload fileUpload;
    @UiField
    HTMLPanel visibleContent;

    private static final String UPLOAD_WITHOUT_AUTOBINDING_URL = "/Crm/springApi/issueUploadFile";
    private static final String UPLOAD_WITH_AUTOBINDING_URL = "/Crm/springApi/uploadFileToCase";
    private FileUploadHandler fileUploadHandler;
    private Long caseId;

    interface FileUploaderUiBinder extends UiBinder<HTMLPanel, FileUploader> {}
    private static FileUploaderUiBinder ourUiBinder = GWT.create(FileUploaderUiBinder.class);
}