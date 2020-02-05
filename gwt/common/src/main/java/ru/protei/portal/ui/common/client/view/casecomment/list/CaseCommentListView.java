package ru.protei.portal.ui.common.client.view.casecomment.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.casecomment.list.AbstractCaseCommentListActivity;
import ru.protei.portal.ui.common.client.activity.casecomment.list.AbstractCaseCommentListView;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.lang.TimeElapsedTypeLang;
import ru.protei.portal.ui.common.client.view.selector.ElapsedTimeTypeSelector;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.HasAttachmentListHandlers;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveHandler;
import ru.protei.portal.ui.common.client.widget.dndautoresizetextarea.DndAutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.imagepastetextarea.event.PasteEvent;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.timefield.HasTime;
import ru.protei.portal.ui.common.client.widget.timefield.TimeTextBox;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;

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
        timeElapsedType.setDisplayOptionCreator( type -> new DisplayOption( (type == null || En_TimeElapsedType.NONE.equals( type )) ? lang.issueCommentElapsedTimeTypeLabel() : elapsedTimeTypeLang.getName( type ) ) );
        timeElapsedType.fillOptions();
        comment.setOverlayText(lang.dropFilesHere());
        comment.setDropZonePanel(messageBlock);
        ensureDebugIds();
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
        newMessage.setVisible(value);
    }

    @Override
    public HasTime timeElapsed() {
        return timeElapsed;
    }

    @Override
    public HasValue<En_TimeElapsedType> timeElapsedType() {
        return timeElapsedType;
    }

    @Override
    public void clearTimeElapsed() {
        timeElapsed.setValue(null);
        timeElapsedType.setValue( null );
    }

    public void setTimeElapsedVisibility(boolean visible) {
        timeElapsed.setVisible(visible);
        timeElapsedType.setVisible(visible);
        if (visible) {
            timeElapsedInfoContainer.removeClassName("hide");
        } else {
            timeElapsedInfoContainer.addClassName("hide");
        }
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
    public void setMarkupLabel(String label, String link) {
        if (label == null) {
            markupLabel.addClassName("hide");
            markupLink.addStyleName("hide");
        } else {
            markupLabel.setInnerText(label);
            markupLabel.removeClassName("hide");

            markupLink.setHref(link);
            markupLink.removeStyleName("hide");
        }
    }

    @Override
    public boolean isDisplayPreview() {
        return isDisplayPreview.getValue();
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

    @UiHandler({"comment", "timeElapsed"})
    public void onCtrlEnterClicked(AddEvent event) {
        if (activity != null) {
            activity.onSendClicked();
        }
    }

    @UiHandler("comment")
    public void onCommentChanged(ValueChangeEvent<String> event) {
        if (activity != null) {
            activity.onCommentChanged(event.getValue());
        }
    }

    @UiHandler("comment")
    public void onBase64Pasted(PasteEvent event) {
        if (event.getJsons() != null && !event.getJsons().isEmpty()) {
            fileUploader.uploadBase64Files(event.getJsons());
        } else {
            fileUploader.uploadBase64File(event.getJson());
        }
    }

    @UiHandler("isDisplayPreview")
    public void onDisplayPreviewChanged( ClickEvent event ) {
        if (activity != null) {
            activity.onDisplayPreviewChanged( isDisplayPreview.getValue() );
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

    private HasVisibility privacyVisibility = new HasVisibility() {
        @Override
        public boolean isVisible() {
            return privateComment.isVisible();
        }

        @Override
        public void setVisible( boolean b ) {
            privateComment.setVisible( b );
        }
    };

    @Override
    public HasVisibility getPrivacyVisibility() {
        return privacyVisibility;
    }

    @Override
    public HasValue<Boolean> privateComment() {
        return privateComment;
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        commentsContainer.ensureDebugId(DebugIds.CASE_COMMENT.COMMENT_LIST.COMMENTS_LIST);
        newCommentUserImage.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CASE_COMMENT.COMMENT_LIST.USER_ICON);
        comment.ensureDebugId(DebugIds.CASE_COMMENT.COMMENT_LIST.TEXT_INPUT);
        privateComment.ensureDebugId(DebugIds.CASE_COMMENT.COMMENT_LIST.PRIVACY_BUTTON);
        send.ensureDebugId(DebugIds.CASE_COMMENT.COMMENT_LIST.SEND_BUTTON);
        filesUpload.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CASE_COMMENT.COMMENT_LIST.FILES_UPLOAD);
        timeElapsed.ensureDebugId(DebugIds.CASE_COMMENT.COMMENT_LIST.TIME_ELAPSED);
        timeElapsedType.setEnsureDebugId(DebugIds.CASE_COMMENT.COMMENT_LIST.TIME_ELAPSED_TYPE);
    }

    @UiField
    HTMLPanel root;
    @UiField
    DndAutoResizeTextArea comment;
    @UiField
    FlowPanel commentsContainer;
    @UiField
    CheckBox privateComment;
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
    @Inject
    @UiField(provided = true)
    ElapsedTimeTypeSelector timeElapsedType;
    @UiField
    ImageElement icon;
    @UiField
    DivElement commentPreviewContainer;
    @UiField
    DivElement commentPreview;
    @UiField
    DivElement newCommentUserImage;
    @UiField
    DivElement filesUpload;
    @UiField
    Element markupLabel;
    @UiField
    Anchor markupLink;
    @UiField
    ToggleButton isDisplayPreview;
    @UiField
    HTMLPanel messageBlock;
    @UiField
    Element timeElapsedInfoContainer;

    @Inject
    private TimeElapsedTypeLang elapsedTimeTypeLang;
    private AbstractCaseCommentListActivity activity;

    private static CaseCommentListUiBinder ourUiBinder = GWT.create(CaseCommentListUiBinder.class);
    interface CaseCommentListUiBinder extends UiBinder<HTMLPanel, CaseCommentListView> {}
}