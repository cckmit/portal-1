package ru.protei.portal.ui.common.client.widget.uploaderdropzone;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.annotations.IsSafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.Arrays;
import java.util.List;

public class FileDropzoneUploader extends Composite implements HasHTML, HasSafeHtml, HasEnabled {

    public FileDropzoneUploader() {
        this((String) null);
    }

    public FileDropzoneUploader(SafeHtml html){
        this(html.asString());
    }

    public FileDropzoneUploader(@IsSafeHtml String html) {
        initWidget(ourUiBinder.createAndBindUi(this));
        if (StringUtils.isNotEmpty(html)) {
            setHTML(html);
        }
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);
        FormElement.as(form.getElement()).setAcceptCharset("UTF-8");
        form.addSubmitCompleteHandler(this::submitCompleteHandler);
        fileUpload.addChangeHandler(this::changeHandler);
        initHandlers();
    }

    @Override
    public void setHTML(SafeHtml html) {
        setHTML(html.asString());
    }

    @Override
    public String getHTML() {
        return dropzone.getElement().getInnerHTML();
    }

    @Override
    public void setHTML(@IsSafeHtml String html) {
        dropzone.getElement().setInnerHTML(html);
    }

    @Override
    public String getText() {
        return dropzone.getElement().getInnerText();
    }

    @Override
    public void setText(String text) {
        dropzone.getElement().setInnerText(text);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        fileUpload.setEnabled(enabled && !uploading);
        dropzone.removeStyleName(STYLE_DISABLED);
        if (!enabled) {
            dropzone.addStyleName(STYLE_DISABLED);
        }
    }

    public boolean isUploading() {
        return uploading;
    }

    public void setUploading(boolean uploading) {
        this.uploading = uploading;
        fileUpload.setEnabled(enabled && !uploading);
        dropzone.removeStyleName(STYLE_LOADING);
        if (uploading) {
            dropzone.addStyleName(STYLE_LOADING);
        }
    }

    public void setUploadHandler(UploadHandler uploadHandler) {
        this.uploadHandler = uploadHandler;
    }

    public void submitForm(String url) {
        form.setAction(url);
        form.submit();
        setUploading(true);
    }

    public void resetForm() {
        form.reset();
        handleChange();
        setUploading(false);
    }

    public void click() {
        fileUpload.click();
    }

    public void setAccept(String...accept) {
        setAccept(Arrays.asList(accept));
    }

    public void setAccept(List<String> accept) {
        setAccept(StringUtils.join(accept, ","));
    }

    public void setAccept(String accept) {
        fileUpload.getElement().setAttribute("accept", accept);
    }

    public boolean isFileSet() {
        return !StringUtils.isEmpty(fileUpload.getFilename());
    }

    public String getFilename() {
        String[] split = fileUpload.getFilename().split("\\\\");
        return split[split.length - 1];
    }

    public void setEnsureDebugId(String debugId) {
        form.ensureDebugId(debugId);
    }

    protected void initHandlers() {

        dropzone.addDomHandler(event -> {
            event.preventDefault();
            if (!isEnabled() || isUploading()) return;
            dropzone.addStyleName(STYLE_HOVER);
        }, DragOverEvent.getType());

        dropzone.addDomHandler(event -> {
            event.preventDefault();
            dropzone.removeStyleName(STYLE_HOVER);
        }, DragLeaveEvent.getType());

        dropzone.addDomHandler(event -> {
            event.preventDefault();
            event.stopPropagation();
            dropzone.removeStyleName(STYLE_HOVER);
            if (!isEnabled() || isUploading()) return;
            onDropEvent(event.getDataTransfer(), fileUpload.getElement().cast());
            handleChange();
        }, DropEvent.getType());
    }

    protected native void onDropEvent(JavaScriptObject dataTransfer, InputElement input) /*-{
        input.files = dataTransfer.files;
    }-*/;

    protected native void log(String message) /*-{
        console.log(message);
    }-*/;

    protected void submitCompleteHandler(FormPanel.SubmitCompleteEvent event) {
        if (uploadHandler != null) {
            uploadHandler.onComplete(event.getResults());
        }
    }

    protected void changeHandler(ChangeEvent event) {
        handleChange();
    }

    private void handleChange() {
        log("File changed to " + getFilename());
        filenameInfo.setInnerText(getFilename());
        if (uploadHandler != null) {
            uploadHandler.onChange();
        }
    }

    @Inject
    @UiField
    protected Lang lang;

    @UiField
    protected FormPanel form;
    @UiField
    protected FileUpload fileUpload;
    @UiField
    protected HTMLPanel dropzone;
    @UiField
    protected DivElement filenameInfo;

    protected UploadHandler uploadHandler;
    protected boolean enabled = true;
    protected boolean uploading = false;

    protected static final String STYLE_HOVER = "dropzone-hover";
    protected static final String STYLE_LOADING = "dropzone-loading";
    protected static final String STYLE_DISABLED = "dropzone-disabled";
    interface FileDropzoneUploaderUiBinder extends UiBinder<FormPanel, FileDropzoneUploader> {}
    private static FileDropzoneUploaderUiBinder ourUiBinder = GWT.create(FileDropzoneUploaderUiBinder.class);
}
