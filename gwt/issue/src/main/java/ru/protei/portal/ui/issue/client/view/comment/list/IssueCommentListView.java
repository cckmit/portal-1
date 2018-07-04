package ru.protei.portal.ui.issue.client.view.comment.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.HasAttachmentListHandlers;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveHandler;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.timefield.HasTime;
import ru.protei.portal.ui.common.client.widget.timefield.TimeTextBox;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.issue.client.activity.comment.list.AbstractIssueCommentListActivity;
import ru.protei.portal.ui.issue.client.activity.comment.list.AbstractIssueCommentListView;

/**
 * Контейнер для комментариев
 */
public class IssueCommentListView
        extends Composite
        implements AbstractIssueCommentListView, HasAttachmentListHandlers {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        comment.getElement().setAttribute("placeholder", lang.commentAddMessagePlaceholder());
    }

    @Override
    public void setActivity(AbstractIssueCommentListActivity activity) {
        this.activity = activity;
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
    public void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler) {
        fileUploader.setUploadHandler(handler);
    }

    @Override
    public HasAttachments attachmentContainer(){
        return attachmentList;
    }

    @Override
    public void enabledNewComment( boolean value ) {
        comment.setVisible( value );
        send.setVisible( value );
    }

    @Override
    public HasTime timeElapsed() {
        return timeElapsed;
    }

    @Override
    public void clearTimeElapsed() {
        timeElapsed.setValue(null);
    }

    @Override
    public HasVisibility timeElapsedVisibility() {
        return timePanel;
    }

    @Override
    public void clearCommentsContainer() {
        commentsContainer.clear();
        commentsContainer.add( newMessage );
    }

    @Override
    public void addCommentToFront(IsWidget comment) {
        commentsContainer.insert( comment.asWidget(), 1 );
    }

    @Override
    public void removeComment(IsWidget comment) {
        commentsContainer.remove( comment.asWidget() );
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

    @UiHandler("timeBtn")
    public void onTimeBtn(ClickEvent event){
        timePanel.setVisible(!timePanel.isVisible());
    }

    @Override
    public HandlerRegistration addRemoveHandler(RemoveHandler handler) {
        return addHandler( handler, RemoveEvent.getType() );
    }

    @UiField
    HTMLPanel root;
    @UiField
    AutoResizeTextArea comment;
    @UiField
    FlowPanel commentsContainer;
    @UiField
    Button send;
    @UiField
    Button timeBtn;
    @Inject
    @UiField(provided = true)
    AttachmentList attachmentList;
    @Inject
    @UiField(provided = true)
    AttachmentUploader fileUploader;
    @UiField
    Lang lang;
    @UiField
    HTMLPanel newMessage;
    @Inject
    @UiField(provided = true)
    TimeTextBox timeElapsed;
    @UiField
    HTMLPanel timePanel;

    private AbstractIssueCommentListActivity activity;

    private static IssueListUiBinder ourUiBinder = GWT.create(IssueListUiBinder.class);
    interface IssueListUiBinder extends UiBinder<HTMLPanel, IssueCommentListView> {}
}