package ru.protei.portal.ui.common.client.activity.commenthistory;

import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.ent.CommentsAndHistories.CommentOrHistory;
import ru.protei.portal.core.model.event.CaseCommentRemovedClientEvent;
import ru.protei.portal.core.model.event.CaseCommentSavedClientEvent;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.ValidationResult;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.ConfigStorage;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AccountControllerAsync;
import ru.protei.portal.ui.common.client.service.AttachmentControllerAsync;
import ru.protei.portal.ui.common.client.service.CaseCommentControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.client.util.AvatarUtils;
import ru.protei.portal.ui.common.client.widget.uploader.impl.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.uploader.impl.PasteInfo;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static ru.protei.portal.core.model.dict.En_CommentOrHistoryType.COMMENT;
import static ru.protei.portal.core.model.dict.En_CommentOrHistoryType.HISTORY;
import static ru.protei.portal.core.model.helper.CaseCommentUtils.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;
import static ru.protei.portal.ui.common.client.common.DateFormatter.formatDateTime;
import static ru.protei.portal.ui.common.client.util.AttachmentUtils.getRemoveErrorHandler;
import static ru.protei.portal.ui.common.client.util.CommentOrHistoryUtils.getSortedCommentOrHistoryList;
import static ru.protei.portal.ui.common.client.util.MultiTabWidgetUtils.getCommentAndHistorySelectedTabs;

/**
 * Активность списка комментариев
 */
public abstract class CommentAndHistoryListActivity
        implements AbstractCommentAndHistoryListActivity {

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        this.profile = event.profile;
    }

    @Event
    public void onShow(CommentAndHistoryEvents.Show event) {
        this.view = event.view;
        this.view.setActivity( this );
        this.view.setFileUploadHandler(fileUploadhandler);

        this.caseType = event.caseType;
        this.caseId = event.caseId;
        this.textMarkup = event.textMarkup;
        this.isElapsedTimeEnabled = event.isElapsedTimeEnabled;
        this.isModifyEnabled = event.isModifyEnabled;
        this.isPrivateVisible = event.isPrivateVisible;
        this.isPrivateCase = event.isPrivateCase;
        this.isNewCommentEnabled = event.isNewCommentEnabled;

        fireEvent(new CaseCommentItemEvents.Init(
                caseType, caseId, textMarkup,
                isPrivateVisible, isElapsedTimeEnabled,
                isModifyEnabled, view.commentsAndHistoriesContainer(),
                comment -> makeAllowEditValidationString(comment, profile),
                comment -> makeAllowRemoveValidationString(comment, profile)
        ));

        fireEvent(new CaseHistoryEvents.Init(view.commentsAndHistoriesContainer()));

        comment = null;
        tempAttachments.clear();
        unlockSave();

        view.message().setValue(makeCommentText(), true);
        view.attachmentContainer().clear();
        view.clearItemsContainer();
        view.clearTimeElapsed();
        view.setTimeElapsedVisibility(isElapsedTimeEnabled);
        view.setUserIcon(AvatarUtils.getAvatarUrl(profile));
        view.setNewCommentHidden(!isModifyEnabled);
        view.setNewCommentDisabled(!isNewCommentEnabled);
        view.setIssueCommentHelpLink(textMarkup == En_TextMarkup.MARKDOWN ? lang.textMarkdownSupport()
                                                                          : lang.textJiraWikiMarkupSupport(),
                                                                            CrmConstants.IssueCommentHelp.LINK);
        view.setPrivacyTypeSelector(event.extendedPrivacyType);
        view.getPrivacyVisibility().setVisible(isPrivateVisible);

        view.setCaseCreatorId(event.caseCreatorId);
        view.setInitiatorCompanyId(event.initiatorCompanyId);
        view.setMentionEnabled(event.isMentionEnabled);

        reloadItems(caseType, caseId);
    }

    @Event
    public void onReload(CommentAndHistoryEvents.Reload event) {
        reloadItems(caseType, caseId);
    }

    @Event
    public void onCaseCommentRemovedClientEvent( CaseCommentRemovedClientEvent event ) {
        if (!view.isAttached()) return;
        if (!Objects.equals( caseId, event.getCaseObjectId() )) return;
        if (Objects.equals( policyService.getProfileId(), event.getPersonId() )) return;

        fireEvent(new CaseCommentItemEvents.RemoveClientComment(event.getCaseCommentID()));
        view.restyleFirstVisibleItemContainer();
    }

    @Event
    public void onCaseCommentSavedClientEvent( CaseCommentSavedClientEvent event ) {
        if (!view.isAttached()) return;
        if (!Objects.equals( caseId, event.getCaseObjectId() )) return;
        if (Objects.equals( policyService.getProfileId(), event.getPersonId() )) return;

        caseCommentController.getCaseComment(event.getCaseCommentID(), new FluentCallback<CaseComment>()
                .withSuccess(comment -> {
                    if (comment == null) return;
                    if (!view.isAttached()) return;

                    renderTextAsync(comment.getText(), textMarkup, converted -> {
                        comment.setText(converted);
                        fireEvent(new CaseCommentItemEvents.SaveOrUpdateClientComment(comment, isCommentVisible()));
                        view.restyleFirstVisibleItemContainer();
                    });
                })
        );
    }

    @Event
    public void onDisableNewComment(CommentAndHistoryEvents.DisableNewComment event) {
        this.isNewCommentEnabled = false;
        view.setNewCommentDisabled(true);
    }

    @Event
    public void onEditComment(CaseCommentItemEvents.EditComment event) {
        commentCompleteEditConsumer = event.resultConsumer;

        comment = event.comment;

        view.attachmentContainer().clear();
        tempAttachments.clear();

        Collection<Attachment> commentAttachments = event.itemView.attachmentContainer().getAll();

        if(!commentAttachments.isEmpty()) {
            view.attachmentContainer().add(commentAttachments);
            tempAttachments.addAll(commentAttachments);
        }

        view.message().setValue(event.comment.getText(), true);

        if (isElapsedTimeEnabled && event.comment.getTimeElapsed() != null) {
            view.timeElapsed().setTime(event.comment.getTimeElapsed());
            view.timeElapsedType().setValue(event.comment.getTimeElapsedType());
        }

        view.getPrivacyVisibility().setVisible(false);
        view.privacyType().setValue(event.comment.getPrivacyType());

        view.focus();
    }

    @Event
    public void onReplyComment(CaseCommentItemEvents.ReplyComment event) {
        comment = null;

        accountService.getLoginByPersonId(event.authorId, new FluentCallback<String>()
                .withSuccess(login -> view.message().setValue(appendLogin(view.message().getValue(), login), true))
        );

        view.focus();
    }

    @Event
    public void onRemoveAttachment(CaseCommentItemEvents.RemoveAttachment event) {
        if(comment != null && comment == event.comment) {
            //deleting while editing
            fireEvent(new NotifyEvents.Show(lang.errEditIssueComment(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        removeAttachment(event.attachment.getId(), () -> {
            fireEvent(new AttachmentEvents.Remove(caseId, singletonList(event.attachment)));
            reloadItems(caseType, caseId);
        });
    }

    @Event
    public void onShowItems(CommentAndHistoryEvents.ShowItems event) {
        this.view = event.view;
        displayItems(event.typesToShow);
    }

    @Override
    public void onSendClicked() {
        if (!isNewCommentEnabled) {
            return;
        }
        send();
    }

    @Override
    public void removeTempAttachment(Attachment attachment) {
        Runnable removeTempAttachmentAction = () -> {
            tempAttachments.remove(attachment);
            view.attachmentContainer().remove(attachment);
        };

        if(comment != null && extractIds(comment.getCaseAttachments()).contains(attachment.getId())){
            removeTempAttachmentAction.run();
        }else {
            //deleting the newly created attachment
            removeAttachment(attachment.getId(), removeTempAttachmentAction);
        }
    }

    @Override
    public void onDetachView() {
        attachmentService.clearUploadedAttachmentsCache(new RequestCallback<Void>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(Void aVoid) {}
        });
    }

    @Override
    public void onCommentChanged(String text) {
        if (StringUtils.isNotEmpty(text)) {
            storage.set(makeStorageKey(caseId), text);
        } else {
            storage.remove(makeStorageKey(caseId));
        }
        scheduleChangedPreview();
    }

    @Override
    public void onDisplayPreviewChanged( Boolean isDisplayPreview ) {
        storage.set( IS_PREVIEW_DISPLAYED, String.valueOf( isDisplayPreview ) );
        fireChangedPreview();
    }

    @Override
    public void onAddingCommentHelpLinkClicked() {
        fireEvent(new IssueEvents.ShowIssueCommentHelp());
    }

    @Override
    public void onCancelClicked() {
        storage.remove(makeStorageKey(this.caseId));
        clearCommentFields();
    }

    private void removeAttachment(Long id, Runnable successAction) {
        attachmentService.removeAttachmentEverywhere(caseType, id, new FluentCallback<Long>()
                .withError(getRemoveErrorHandler(this, lang))
                .withSuccess(result -> successAction.run())
        );
    }

    private void addImageToMessage(Integer strPosition, Attachment attach) {
        view.message().setValue(
                addImageInMessage(textMarkup, view.message().getValue(), strPosition, attach));
    }

    private void addTempAttachment(Attachment attach) {
        view.attachmentContainer().add(attach);
        tempAttachments.add(attach);
    }

    private void fillView(List<CaseComment> comments) {
        view.clearItemsContainer();
        view.setNewCommentHidden(!isModifyEnabled);
        view.setNewCommentDisabled(!isNewCommentEnabled);

        view.setCommentPlaceholder(policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW) ?
                lang.commentAddMessageMentionPlaceholder() : lang.commentAddMessagePlaceholder());

        fireEvent(new CaseCommentItemEvents.Clear());
        fireEvent(new CaseCommentItemEvents.FillComments(comments));
    }

    public void fillView(CommentsAndHistories commentsAndHistories) {
        view.clearItemsContainer();

        fireEvent(new CaseCommentItemEvents.Clear());
        fireEvent(new CaseHistoryEvents.Clear());

        view.setNewCommentHidden(!isModifyEnabled);
        view.setNewCommentDisabled(!isNewCommentEnabled);

        view.setCommentPlaceholder(policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW) ?
                lang.commentAddMessageMentionPlaceholder() : lang.commentAddMessagePlaceholder());

        List<CommentOrHistory> commentOrHistoryList
                = getSortedCommentOrHistoryList(commentsAndHistories.getCommentOrHistoryList());

        List<History> histories = new ArrayList<>();
        List<CaseComment> comments = new ArrayList<>();

        for (CommentOrHistory commentOrHistory : commentOrHistoryList) {
            if (commentOrHistory.getCaseComment() != null) {
                flushHistories(histories);
                comments.add(commentOrHistory.getCaseComment());
                continue;
            }

            if (commentOrHistory.getHistory() != null) {
                flushComments(comments);
                histories.add(commentOrHistory.getHistory());
                continue;
            }
        }

        flushHistories(histories);
        flushComments(comments);
    }

    private void flushHistories(List<History> histories) {
        if (histories.isEmpty()) {
            return;
        }

        fireEvent(new CaseHistoryEvents.Fill(histories));
        histories.clear();
    }

    private void flushComments(List<CaseComment> comments) {
        if (comments.isEmpty()) {
            return;
        }

        fireEvent(new CaseCommentItemEvents.FillComments(comments));
        comments.clear();
    }

    private List<Long> extractIds(Collection<CaseAttachment> list){
        return list == null || list.isEmpty()?
                Collections.emptyList():
                list.stream().map(CaseAttachment::getAttachmentId).collect(Collectors.toList());
    }

    private void send() {
        if (isLockedSave()) {
            return;
        }
        lockSave();

        ValidationResult validationResult = validate();
        if (!validationResult.isValid()) {
            unlockSave();
            fireEvent(new NotifyEvents.Show(validationResult.getMessage(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        comment = buildCaseComment();

        boolean isEdit = comment.getId() != null;
        caseCommentController.saveCaseComment(caseType, comment, new FluentCallback<CaseComment>()
                .withError((throwable, defaultErrorHandler, status) -> {
                    unlockSave();
                    defaultErrorHandler.accept(throwable);
                })
                .withSuccess(result -> {
                    unlockSave();
                    onCommentSent(isEdit, result);
                })
        );
    }

    private ValidationResult validate() {
        if (StringUtils.isBlank(view.message().getValue())) {
            return ValidationResult.error().withMessage(lang.commentEmpty());
        }

        // PORTAL-1138
        if (view.timeElapsedType().getValue() != null && view.timeElapsed().getTime() == null) {
            return ValidationResult.error().withMessage(lang.errorNeedFeelTimeElapsed());
        }

        return ValidationResult.ok();
    }

    private String makeAllowEditValidationString(CaseComment caseComment, Profile profile) {
        return makeAllowEditRemoveValidationString(caseComment, profile, true);
    }

    private String makeAllowRemoveValidationString(CaseComment caseComment, Profile profile) {
        return makeAllowEditRemoveValidationString(caseComment, profile, false);
    }

    private String makeAllowEditRemoveValidationString(CaseComment caseComment, Profile profile, boolean isEdit) {
        if(caseComment == comment) {
            //deleting while editing
            return lang.errEditIssueComment();
        }

        if ( !isEnableEditCommon( caseComment, profile.getId() ) ) {
            return lang.errEditIssueCommentNotAllowed();
        }


        if ( !isEnableEditByTime(caseComment) ) {
            return isEdit ? lang.errEditIssueCommentByTime() : lang.errRemoveIssueCommentByTime() ;
        }

        return null;
    }

    private CaseComment buildCaseComment() {
        boolean isNew = this.comment == null;

        CaseComment comment;
        if (isNew) {
            comment = new CaseComment();
            comment.setAuthorId(profile.getId());
        } else {
            comment = this.comment;
        }
        Long commentId = comment.getId();
        En_TimeElapsedType elapsedType = view.timeElapsedType().getValue();
        comment.setCaseId(caseId);
        comment.setText(view.message().getValue());
        comment.setTimeElapsed(view.timeElapsed().getTime());
        comment.setTimeElapsedType(elapsedType != null ? elapsedType : En_TimeElapsedType.NONE);
        if (isNew || comment.getPrivacyType() == null) {
            comment.setPrivacyType(isPrivateCase ? En_CaseCommentPrivacyType.PRIVATE : view.privacyType().getValue());
        }
        comment.setCaseAttachments(tempAttachments.stream()
                .map(a -> new CaseAttachment(caseId, a.getId(), commentId))
                .collect(Collectors.toList())
        );
        return comment;
    }

    private void clearCommentFields() {
        comment = null;
        view.message().setValue(null, true);
        view.attachmentContainer().clear();
        view.clearTimeElapsed();
        tempAttachments.clear();
        view.getPrivacyVisibility().setVisible(isPrivateVisible);
        view.privacyType().setValue(En_CaseCommentPrivacyType.PUBLIC);
    }

    private void onCommentSent(boolean isEdit, CaseComment caseComment) {

        storage.remove(makeStorageKey(caseComment.getCaseId()));

        caseComment.setCaseAttachments(comment.getCaseAttachments());
        stream(tempAttachments).forEach(attachment -> attachment.setPrivate(comment.isPrivateComment()));

        if (isEdit) {
            commentCompleteEditConsumer.accept(comment, tempAttachments);
        } else {
            fireEvent(new AttachmentEvents.Add(caseId, tempAttachments));
            fireEvent(new CaseCommentItemEvents.CreateComment(caseComment, isCommentVisible()));
            view.restyleFirstVisibleItemContainer();
        }

        clearCommentFields();
    }

    private boolean isCommentVisible() {
        if (!isCommentWithHistoryCase(caseType)) {
            return true;
        }

        return getCommentAndHistorySelectedTabs(storage).contains(COMMENT);
    }

    private void lockSave() {
        saving = true;
        view.sendEnabled().setEnabled(false);
    }

    private void unlockSave() {
        saving = false;
        view.sendEnabled().setEnabled(true);
    }

    private boolean isLockedSave() {
        return saving;
    }

    private void scheduleChangedPreview() {
        changedPreviewTimer.cancel();
        changedPreviewTimer.schedule(PREVIEW_CHANGE_DELAY_MS);
    }

    private void fireChangedPreview() {
        String text = view.message().getValue();

        if (StringUtils.isBlank(text)) {
            view.setPreviewVisible(false);
            return;
        }

        view.setPreviewVisible( true );
        if(!view.isDisplayPreview()){
            view.setPreviewText( "" );
            return;
        }

        renderTextAsync(text, textMarkup, converted -> {
            if (StringUtils.isBlank(converted)) {
                view.setPreviewVisible(false);
                return;
            }
            view.setPreviewText(converted);
            view.setPreviewVisible(true);
        });
    }

    private String makeCommentText() {
        String text = storage.get(makeStorageKey(caseId));
        return isEmpty(text) ? null : text;
    }

    private String makeStorageKey(Long id){
        return STORAGE_CASE_COMMENT_PREFIX + id;
    }

    private void renderTextAsync(String text, En_TextMarkup textMarkup, Consumer<String> consumer) {
        textRenderController.render(text, textMarkup, true, new FluentCallback<String>()
                .withError(throwable -> consumer.accept(text))
                .withSuccess(consumer));
    }

    private void reloadItems(En_CaseType caseType, Long caseId) {
        if (isCommentWithHistoryCase(caseType)) {
            caseCommentController.getCommentsAndHistories(caseType, caseId, new FluentCallback<CommentsAndHistories>()
                    .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errNotFound(), NotifyEvents.NotifyType.ERROR)))
                    .withSuccess(commentsAndHistories -> {
                        fillView(commentsAndHistories);
                        displayItems(getCommentAndHistorySelectedTabs(storage));
                    })
            );

            return;
        }

        caseCommentController.getCaseComments(caseType, caseId, new FluentCallback<List<CaseComment>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errNotFound(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(comments -> {
                    fillView(comments);
                    displayItems(singletonList(COMMENT));
                })
        );
    }

    private boolean isCommentWithHistoryCase(En_CaseType caseType) {
        if (En_CaseType.CRM_SUPPORT.equals(caseType)) {
            return true;
        }

        if (En_CaseType.EMPLOYEE_REGISTRATION.equals(caseType)) {
            return true;
        }

        if (En_CaseType.DELIVERY.equals(caseType)) {
            return true;
        }

        return false;
    }

    private void displayItems(List<En_CommentOrHistoryType> typesToShow) {
        hideCommentItems();
        hideHistoryItems();

        view.setNewCommentHidden(!typesToShow.contains(COMMENT));

        typesToShow.forEach(type -> {
            if (type.equals(COMMENT)) {
                showCommentItems();
            }

            if (type.equals(HISTORY)) {
                showHistoryItems();
            }
        });

        view.restyleFirstVisibleItemContainer();
    }

    private void showCommentItems() {
        fireEvent(new CaseCommentItemEvents.Show());
    }

    private void hideCommentItems() {
        fireEvent(new CaseCommentItemEvents.Hide());
    }

    private void showHistoryItems() {
        fireEvent(new CaseHistoryEvents.Show());
    }

    private void hideHistoryItems() {
        fireEvent(new CaseHistoryEvents.Hide());
    }

    private final Timer changedPreviewTimer = new Timer() {
        @Override
        public void run() {
            fireChangedPreview();
        }
    };

    AttachmentUploader.FileUploadHandler fileUploadhandler = new AttachmentUploader.FileUploadHandler() {
        @Override
        public void onSuccess(Attachment attachment, PasteInfo pasteInfo) {
            if (pasteInfo != null && attachment.getMimeType().startsWith("image/")) {
                addImageToMessage(pasteInfo.strPosition, attachment);
            }
            addTempAttachment(attachment);
        }

        @Override
        public void onError(En_FileUploadStatus status, String details) {
            if (En_FileUploadStatus.SIZE_EXCEED_ERROR.equals(status)) {
                fireEvent(new NotifyEvents.Show(lang.uploadFileSizeExceed() + " (" + details + "Mb)", NotifyEvents.NotifyType.ERROR));
            } else {
                fireEvent(new NotifyEvents.Show(lang.uploadFileError(), NotifyEvents.NotifyType.ERROR));
            }
        }
    };

    @Inject
    Lang lang;
    @Inject
    CaseCommentControllerAsync caseCommentController;
    AbstractCommentAndHistoryListView view;
    @Inject
    AttachmentControllerAsync attachmentService;
    @Inject
    TextRenderControllerAsync textRenderController;
    @Inject
    private LocalStorageService storage;
    @Inject
    AccountControllerAsync accountService;

    @Inject
    ConfigStorage configStorage;
    @Inject
    PolicyService policyService;

    private CaseComment comment;

    private Profile profile;

    private En_CaseType caseType;
    private En_TextMarkup textMarkup;
    private boolean saving = false;
    private boolean isElapsedTimeEnabled = false;
    private boolean isModifyEnabled = true;
    private Long caseId;
    private boolean isPrivateVisible = false;
    private boolean isPrivateCase = false;
    private boolean isNewCommentEnabled = true;
    private BiConsumer<CaseComment, Collection<Attachment>> commentCompleteEditConsumer;

    private Collection<Attachment> tempAttachments = new ArrayList<>();

    private final static int PREVIEW_CHANGE_DELAY_MS = 200;

    private final String STORAGE_CASE_COMMENT_PREFIX = "CaseСomment_";
    private final String IS_PREVIEW_DISPLAYED = STORAGE_CASE_COMMENT_PREFIX+"is_preview_displayed";

    private static final Logger log = Logger.getLogger( CommentAndHistoryListActivity.class.getName() );
}
