package ru.protei.portal.ui.issue.client.view.comment.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.uploader.FileUploader;
import ru.protei.portal.ui.issue.client.activity.comment.list.AbstractIssueCommentListActivity;
import ru.protei.portal.ui.issue.client.activity.comment.list.AbstractIssueCommentListView;

/**
 * Контейнер для комментариев
 */
public class IssueCommentListView
        extends Composite
        implements AbstractIssueCommentListView {

    @Inject
    public void onInit( ) {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        comment.getElement().setAttribute( "placeholder", lang.commentAddMessagePlaceholder() );
    }

    @Override
    public void setActivity( AbstractIssueCommentListActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getCommentsContainer() {
        return commentsContainer;
    }

    @Override
    public HasValue< String > message() {
        return comment;
    }

    @Override
    public void focus() {
        comment.setFocus( true );
    }

    @Override
    public void setFileUploadHandler(FileUploader.FileUploadHandler handler) {
        fileUploader.setFileUploadHandler(handler);
    }

    @Override
    public HasAttachments attachmentContainer(){
        return attachmentList;
    }

    @UiHandler( "send" )
    public void onSendClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onSendClicked();
        }
    }

    @UiHandler( "send" )
    public void onEditLastMessage( KeyUpEvent event ) {
        if ( event.getNativeKeyCode() != KeyCodes.KEY_UP ) {
            return;
        }

        if ( activity != null ) {
            activity.onEditLastMessage();
        }
    }

    @UiHandler("attachmentList")
    public void onRemoveAttachment(RemoveEvent event){
        activity.removeTempAttachment(event.getAttachment());
    }

    @UiField
    HTMLPanel root;
    @UiField
    TextArea comment;
    @UiField
    HTMLPanel commentsContainer;
    @UiField
    Button send;
    @Inject
    @UiField(provided = true)
    AttachmentList attachmentList;
    @Inject
    @UiField(provided = true)
    FileUploader fileUploader;
    @UiField
    Lang lang;

    private AbstractIssueCommentListActivity activity;

    private static IssueListUiBinder ourUiBinder = GWT.create(IssueListUiBinder.class);
    interface IssueListUiBinder extends UiBinder<HTMLPanel, IssueCommentListView> {}
}