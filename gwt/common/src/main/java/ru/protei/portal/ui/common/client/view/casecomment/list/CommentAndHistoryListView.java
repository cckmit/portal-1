package ru.protei.portal.ui.common.client.view.casecomment.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseCommentPrivacyType;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.commenthistory.AbstractCommentAndHistoryListActivity;
import ru.protei.portal.ui.common.client.activity.commenthistory.AbstractCommentAndHistoryListView;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.lang.TimeElapsedTypeLang;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorModel;
import ru.protei.portal.ui.common.client.view.selector.ElapsedTimeTypeSelector;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.HasAttachmentListHandlers;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveHandler;
import ru.protei.portal.ui.common.client.widget.imagepastetextarea.event.PasteEvent;
import ru.protei.portal.ui.common.client.widget.mentioningtextarea.MentioningTextArea;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.privacy.PrivacyTypeSelector;
import ru.protei.portal.ui.common.client.widget.timefield.HasTime;
import ru.protei.portal.ui.common.client.widget.timefield.TimeTextBox;
import ru.protei.portal.ui.common.client.widget.uploader.impl.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.uploader.impl.PasteInfo;

import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;
import static ru.protei.portal.core.model.util.CrmConstants.Style.HIDE;

/**
 * Контейнер для комментариев
 */
public class CommentAndHistoryListView
        extends Composite
        implements AbstractCommentAndHistoryListView, HasAttachmentListHandlers {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        timeElapsedType.setDisplayOptionCreator( type -> new DisplayOption( (type == null || En_TimeElapsedType.NONE.equals( type )) ? lang.issueCommentElapsedTimeTypeLabel() : elapsedTimeTypeLang.getName( type ) ) );
        timeElapsedType.fillOptions();
        comment.setOverlayText(lang.dropFilesHere());
        comment.setDropZonePanel(messageBlock);
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractCommentAndHistoryListActivity activity) {
        this.activity = activity;
        attachmentList.setActivity(activity);
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
    public void setNewCommentHidden(boolean isHidden) {
        newMessage.setVisible(!isHidden);
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

    @Override
    public void setTimeElapsedVisibility(boolean visible) {
        timeElapsed.setVisible(visible);
        timeElapsedType.setVisible(visible);
    }

    @Override
    public void setNewCommentDisabled(boolean isDisabled) {
        newCommentContainer.setVisible(!isDisabled);
        newCommentDisabledContainer.setVisible(isDisabled);
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
            commentPreviewContainer.removeClassName( HIDE );
        } else {
            commentPreviewContainer.addClassName( HIDE );
        }
    }

    @Override
    public void setIssueCommentHelpLink(String label, String link) {
        if (label == null) {
            helpLabel.addClassName( HIDE );
            helpLink.addStyleName( HIDE );
        } else {
            helpLabel.setInnerHTML(label);
            helpLabel.removeClassName( HIDE );

            helpLink.setHref(link);
            helpLink.removeStyleName( HIDE );
        }
    }

    @Override
    public boolean isDisplayPreview() {
        return isDisplayPreview.getValue();
    }

    @Override
    public void clearItemsContainer() {
        commentsAndHistoriesContainer.clear();
    }

    @Override
    public void removeComment(IsWidget comment) {
        commentsAndHistoriesContainer.remove( comment.asWidget() );
    }

    @Override
    public void setCaseCreatorId(Long personId) {
        comment.setPersonId(personId);
    }

    @Override
    public void setInitiatorCompanyId(Long initiatorCompanyId) {
        comment.setInitiatorCompanyId(initiatorCompanyId);
    }

    @Override
    public void setMentionEnabled(boolean isMentionEnabled) {
        comment.setIsMentionEnabled(isMentionEnabled);
    }

    @Override
    public void setCommentPlaceholder(String placeholder) {
        comment.getElement().setAttribute("placeholder", placeholder);
    }

    @Override
    public FlowPanel commentsAndHistoriesContainer() {
        return commentsAndHistoriesContainer;
    }

    @UiHandler( "send" )
    public void onSendClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onSendClicked();
        }
    }

    @UiHandler( "cancel" )
    public void onCancelClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onCancelClicked();
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
            fileUploader.uploadBase64Files(event.getJsons(), new PasteInfo(event.getStrPos()));
        } else {
            fileUploader.uploadBase64File(event.getJson(), new PasteInfo(event.getStrPos()));
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

    @Override
    public HasVisibility getPrivacyVisibility() {
        return privacyType;
    }

    @Override
    public void setJiraWorkflowWarningVisible(boolean isVisible) {
        if (isVisible) {
            jiraWorkflowWarning.removeClassName(CrmConstants.Style.HIDE);
        } else {
            jiraWorkflowWarning.addClassName(CrmConstants.Style.HIDE);
        }
    }

    @Override
    public void setPrivacyTypeSelector(boolean isExtendedPrivacyType) {
        privacyType.setModel( makeSelectorModel(isExtendedPrivacyType) );
        privacyType.setValue(En_CaseCommentPrivacyType.PUBLIC);
    }

    @Override
    public HasValue<En_CaseCommentPrivacyType> privacyType() {
        return privacyType;
    }

    @Override
    public void restyleFirstVisibleItemContainer() {
        removeZeroMarginFromAllContainers(commentsAndHistoriesContainer);
        addZeroMarginToFirstVisibleContainer(commentsAndHistoriesContainer);
    }

    private void addZeroMarginToFirstVisibleContainer(FlowPanel itemsContainer) {
        for (Widget nextWidget : itemsContainer) {
            if (nextWidget.isVisible()) {
                nextWidget.addStyleName("m-t-0");
                break;
            }
        }
    }

    private void removeZeroMarginFromAllContainers(FlowPanel itemsContainer) {
        itemsContainer.forEach(nextWidget -> nextWidget.removeStyleName("m-t-0"));
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        commentsAndHistoriesContainer.ensureDebugId(DebugIds.CASE_COMMENT.COMMENT_LIST.ITEMS_LIST);
        newMessage.ensureDebugId(DebugIds.CASE_COMMENT.COMMENT_LIST.NEW_MESSAGE);
        newCommentUserImage.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CASE_COMMENT.COMMENT_LIST.USER_ICON);
        comment.ensureDebugId(DebugIds.CASE_COMMENT.COMMENT_LIST.TEXT_INPUT);
        send.ensureDebugId(DebugIds.CASE_COMMENT.COMMENT_LIST.SEND_BUTTON);
        cancel.ensureDebugId(DebugIds.CASE_COMMENT.COMMENT_LIST.CANCEL_BUTTON);
        filesUpload.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CASE_COMMENT.COMMENT_LIST.FILES_UPLOAD);
        timeElapsed.ensureDebugId(DebugIds.CASE_COMMENT.COMMENT_LIST.TIME_ELAPSED);
        timeElapsedType.setEnsureDebugId(DebugIds.CASE_COMMENT.COMMENT_LIST.TIME_ELAPSED_TYPE);
        privacyType.setEnsureDebugId(DebugIds.CASE_COMMENT.COMMENT_LIST.PRIVACY_BUTTON);
    }

    private SelectorModel<En_CaseCommentPrivacyType> makeSelectorModel(boolean isExtendedPrivacyType) {
        return elementIndex -> {
            List<En_CaseCommentPrivacyType> list;
            if (isExtendedPrivacyType) {
                list = En_CaseCommentPrivacyType.extendPrivacyType();
            } else {
                list = En_CaseCommentPrivacyType.simplePrivacyType();
            }
            if (size(list) <= elementIndex) return null;
            return list.get(elementIndex);
        };
    }

    @UiField
    HTMLPanel root;
    @Inject
    @UiField(provided = true)
    MentioningTextArea comment;
    @UiField
    FlowPanel commentsAndHistoriesContainer;
    @Inject
    @UiField(provided = true)
    PrivacyTypeSelector privacyType;
    @UiField
    Button send;
    @UiField
    Button cancel;
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
    Element helpLabel;
    @UiField
    Anchor helpLink;
    @UiField
    ToggleButton isDisplayPreview;
    @UiField
    HTMLPanel messageBlock;
    @UiField
    HTMLPanel newCommentContainer;
    @UiField
    HTMLPanel newCommentDisabledContainer;
    @UiField
    DivElement jiraWorkflowWarning;

    @Inject
    private TimeElapsedTypeLang elapsedTimeTypeLang;
    private AbstractCommentAndHistoryListActivity activity;

    private static CaseCommentListUiBinder ourUiBinder = GWT.create(CaseCommentListUiBinder.class);
    interface CaseCommentListUiBinder extends UiBinder<HTMLPanel, CommentAndHistoryListView> {}
}
