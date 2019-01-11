package ru.protei.portal.ui.common.client.view.casecomment.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.HasAttachmentListHandlers;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveHandler;
import ru.protei.portal.ui.common.client.widget.markdown.textarea.MarkdownTextArea;
import ru.protei.portal.ui.common.client.widget.markdown.textarea.event.ChangedPreviewEvent;
import ru.protei.portal.ui.common.client.widget.timefield.HasTime;
import ru.protei.portal.ui.common.client.widget.timefield.TimeTextBox;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.client.activity.casecomment.list.AbstractCaseCommentListActivity;
import ru.protei.portal.ui.common.client.activity.casecomment.list.AbstractCaseCommentListView;

/**
 * Контейнер для комментариев
 */
public class CaseCommentListView
        extends Composite
        implements AbstractCaseCommentListView, HasAttachmentListHandlers {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        comment.getElement().setAttribute("placeholder", lang.commentAddMessagePlaceholder());
    }

    @Override
    public void setActivity(AbstractCaseCommentListActivity activity) {
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
    public void setEnabledAttachAndComment(boolean isEnabled) {
        newMessage.setVisible(isEnabled);
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
        return timeElapsed;
    }

    @Override
    public void setUserIcon(String iconSrc) {
        this.icon.setSrc( iconSrc );
    }

    @Override
    public HasEnabled sendEnabled() {
        return send;
    }

    @Override
    public void setPreviewText(String text) {
        commentPreview.setInnerHTML(text);
    }

    @Override
    public void setPreviewVisible(boolean isVisible) {
        if (isVisible) {
            commentPreviewContainer.removeClassName("hide");
        } else {
            commentPreviewContainer.addClassName("hide");
        }
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

    @UiHandler("comment")
    public void onCtrlEnterClicked(AddEvent event) {
        if (activity != null) {
            activity.onSendClicked();
        }
    }

    @UiHandler("comment")
    public void onPreviewChanged(ChangedPreviewEvent event) {
        if (activity != null) {
            activity.onPreviewChanged(event.getPreviewText());
        }
    }

    @Override
    public HandlerRegistration addRemoveHandler(RemoveHandler handler) {
        return addHandler( handler, RemoveEvent.getType() );
    }

    @Override
    protected void onDetach() {
        super.onDetach();

        if ( activity != null ) {
            activity.onDetachView();
        }
    }

    @UiField
    HTMLPanel root;
    @UiField
    MarkdownTextArea comment;
    @UiField
    FlowPanel commentsContainer;
    @UiField
    Button send;
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
    ImageElement icon;
    @UiField
    DivElement commentPreviewContainer;
    @UiField
    DivElement commentPreview;

    private AbstractCaseCommentListActivity activity;

    private static CaseCommentListUiBinder ourUiBinder = GWT.create(CaseCommentListUiBinder.class);
    interface CaseCommentListUiBinder extends UiBinder<HTMLPanel, CaseCommentListView> {}
}