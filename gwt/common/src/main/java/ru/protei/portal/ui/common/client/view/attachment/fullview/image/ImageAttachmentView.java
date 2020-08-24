package ru.protei.portal.ui.common.client.view.attachment.fullview.image;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.attachment.AbstractAttachmentList;
import ru.protei.portal.ui.common.client.activity.attachment.fullview.AbstractAttachmentFullView;

import java.util.Date;

import static ru.protei.portal.core.model.util.CrmConstants.BYTES_IN_MEGABYTE;
import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class ImageAttachmentView extends Composite implements AbstractAttachmentFullView {
    public ImageAttachmentView() {
        initWidget(ourUiBinder.createAndBindUi(this));
        root.addDomHandler(event -> activity.onShowPreview(new Image(picture.getUrl())), ClickEvent.getType());
        setTestAttributes();
    }

    @Override
    public void setActivity(AbstractAttachmentList activity) {
        this.activity = activity;
    }

    @Override
    public void setDownloadUrl(String url) {
        this.url = url;
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName.setInnerText(fileName);
    }

    @Override
    public void setFileSize(long bytes) {
        float MB = bytes / BYTES_IN_MEGABYTE;
        if (MB < 1 / 100f) {
            MB = 1 / 100f; // для JS
        }

        NumberFormat numberFormat = NumberFormat.getFormat("#.##");

        this.fileSize.setInnerText(numberFormat.format(MB).replace(",", ".") + " MB, ");
    }

    @Override
    public void setCreationInfo(String author, Date created) {
        DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("dd.MM.yyyy HH:mm");
        createdDate.setInnerText(dateTimeFormat.format(created));
        this.authorName.setInnerText(author);
    }

    @Override
    public void setPicture(String url) {
        picture.setUrl(url);
    }

    @Override
    public void setAuthorAvatarUrl(String url) {
        authorAvatar.setUrl(url);
    }

    @Override
    public HasVisibility removeButtonVisibility() {
        return deleteButton;
    }

    @UiHandler("deleteButton")
    public void onRemove(ClickEvent event) {
        event.stopPropagation();
        if (activity != null) {
            activity.onAttachmentRemove(this);
        }
    }

    @UiHandler("downloadButton")
    public void onDownloadButtonClicked(ClickEvent event) {
        event.stopPropagation();
        Window.open(url, null, null);
    }

    private void setTestAttributes() {
        deleteButton.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ATTACHMENT.DELETE);
        downloadButton.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ATTACHMENT.DOWNLOAD);
        fileName.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ATTACHMENT.NAME);
        fileSize.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ATTACHMENT.SIZE);
    }

    @UiField
    Image picture;
    @UiField
    Button deleteButton;
    @UiField
    Element fileName;
    @UiField
    SpanElement fileSize;
    @UiField
    Button downloadButton;
    @UiField
    Image authorAvatar;
    @UiField
    Element authorName;
    @UiField
    Element fileInfo;
    @UiField
    HTMLPanel root;
    @UiField
    SpanElement createdDate;

    private AbstractAttachmentList activity;

    private String url;

    interface FullAttachmentViewUiBinder extends UiBinder<HTMLPanel, ImageAttachmentView> {}
    private static FullAttachmentViewUiBinder ourUiBinder = GWT.create(FullAttachmentViewUiBinder.class);
}
