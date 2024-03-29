package ru.protei.portal.ui.common.client.widget.uploader.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.annotations.IsSafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.helper.StringUtils;

/**
 * Загрузчик файлов
 */
public abstract class FileUploader extends Composite implements HasHTML, HasSafeHtml {

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
        FormElement.as(form.getElement()).setAcceptCharset("UTF-8");

        form.addSubmitCompleteHandler(this::submitCompleteHandler);
        fileUpload.addChangeHandler(this::changeHandler);
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

    public void initUploading() {
        fileUpload.click();
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

    protected abstract void submitCompleteHandler(FormPanel.SubmitCompleteEvent event);

    protected abstract void changeHandler(ChangeEvent event);

    @UiField
    protected FormPanel form;
    @UiField
    protected FileUpload fileUpload;
    @UiField
    protected HTMLPanel visibleContent;

    interface FileUploaderUiBinder extends UiBinder<HTMLPanel, FileUploader> {}
    private static FileUploaderUiBinder ourUiBinder = GWT.create(FileUploaderUiBinder.class);
}
