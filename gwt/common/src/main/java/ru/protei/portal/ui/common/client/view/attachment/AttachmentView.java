package ru.protei.portal.ui.common.client.view.attachment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.activity.attachment.AbstractAttachmentActivity;
import ru.protei.portal.ui.common.client.activity.attachment.AbstractAttachmentView;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.Date;

/**
 * Created by bondarenko on 28.12.16.
 */
public class AttachmentView extends Composite implements AbstractAttachmentView {
    public AttachmentView() {
        initWidget(ourUiBinder.createAndBindUi(this));
        picture.addLoadHandler(event -> {
            thumbs.removeClassName("icon-verifiable");
        });
    }

    @Override
    public void setActivity(AbstractAttachmentActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setDownloadUrl(String url){
        downloadButton.setHref(url);
        downloadFullButton.setHref(url);
    }

    @Override
    public void setFileName(String fileName){
        this.fileName.setInnerText(fileName);
    }

    @Override
    public void setFileSize(long B){
        float MB = (int)(B / 1048576f * 100) / 100f;
        if(MB == 0){
            MB = 1 / 100f; // для JS
        }

        this.fileSize.setInnerText("("+ MB + " MB)");
    }

    @Override
    public void setCreationInfo(String author, Date created){
        String dateCreation = created.toLocaleString().replace(",","");
        root.setTitle(lang.attachmentAuthor() +" "+ author +", "+ dateCreation);
    }

    @Override
    public void setPicture(String url) {
        picture.setUrl(url);
    }

    @UiHandler("deleteButton")
    public void onRemove(ClickEvent event){
        if(activity != null)
            activity.onAttachmentRemove(this);
    }

    @UiHandler("showPreviewButton")
    public void onShowPreview(ClickEvent event){
        if(activity != null)
            activity.onShowPreview(
                new Image(picture.getUrl())
            );
    }

    @UiField
    Image picture;
    @UiField
    Anchor deleteButton;
    @UiField
    Anchor showPreviewButton;
    @UiField
    Anchor downloadFullButton;
    @UiField
    SpanElement fileName;
    @UiField
    SpanElement fileSize;
    @UiField
    Anchor downloadButton;
    @UiField
    DivElement attachControls;
    @UiField
    HTMLPanel root;
    @UiField
    DivElement thumbs;
    @Inject
    Lang lang;

    AbstractAttachmentActivity activity;

    interface AttachmentViewUiBinder extends UiBinder<HTMLPanel, AttachmentView> {}
    private static AttachmentViewUiBinder ourUiBinder = GWT.create(AttachmentViewUiBinder.class);
}