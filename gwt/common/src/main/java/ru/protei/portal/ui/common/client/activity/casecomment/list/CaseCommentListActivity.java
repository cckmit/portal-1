package ru.protei.portal.ui.common.client.activity.casecomment.list;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseAttachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.event.CaseCommentSavedClientEvent;
import ru.protei.portal.core.model.event.CaseCommentRemovedClientEvent;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.core.model.util.ValidationResult;
import ru.protei.portal.ui.common.client.activity.casecomment.item.AbstractCaseCommentItemActivity;
import ru.protei.portal.ui.common.client.activity.casecomment.item.AbstractCaseCommentItemView;
import ru.protei.portal.ui.common.client.activity.caselink.CaseLinkProvider;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.ConfigStorage;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.lang.TimeElapsedTypeLang;
import ru.protei.portal.ui.common.client.service.AccountControllerAsync;
import ru.protei.portal.ui.common.client.service.AttachmentControllerAsync;
import ru.protei.portal.ui.common.client.util.AvatarUtils;
import ru.protei.portal.ui.common.client.service.CaseCommentControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.client.view.casecomment.item.CaseCommentItemView;
import ru.protei.portal.ui.common.client.widget.timefield.WorkTimeFormatter;
import ru.protei.portal.ui.common.client.widget.uploader.impl.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.uploader.impl.PasteInfo;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;
import static ru.protei.portal.core.model.helper.CaseCommentUtils.*;

/**
 * Активность списка комментариев
 */
public abstract class CaseCommentListActivity
        implements AbstractCaseCommentListActivity, AbstractCaseCommentItemActivity {

    @Inject
    public void onInit() {
        view.setActivity( this );
        view.setFileUploadHandler(new AttachmentUploader.FileUploadHandler() {
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
                }
                else {
                    fireEvent(new NotifyEvents.Show(lang.uploadFileError(), NotifyEvents.NotifyType.ERROR));
                }
            }
        });
        workTimeFormatter = new WorkTimeFormatter(lang);
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        this.profile = event.profile;
    }

    @Event
    public void onShow(CaseCommentEvents.Show event) {
        event.parent.clear();
        event.parent.add(view.asWidget());

        this.caseType = event.caseType;
        this.caseId = event.caseId;
        this.textMarkup = event.textMarkup;
        this.isElapsedTimeEnabled = event.isElapsedTimeEnabled;
        this.isModifyEnabled = event.isModifyEnabled;
        this.isPrivateVisible = event.isPrivateVisible;
        this.isPrivateCase = event.isPrivateCase;
        this.isNewCommentEnabled = event.isNewCommentEnabled;

        comment = null;
        lastCommentView = null;
        tempAttachments.clear();
        unlockSave();

        view.message().setValue(makeCommentText(null), true);
        view.attachmentContainer().clear();
        view.clearCommentsContainer();
        view.clearTimeElapsed();
        view.setTimeElapsedVisibility(isElapsedTimeEnabled);
        view.setUserIcon(AvatarUtils.getAvatarUrl(profile));
        view.setNewCommentHidden(!isModifyEnabled);
        view.setNewCommentDisabled(!isNewCommentEnabled);
        if (textMarkup == En_TextMarkup.MARKDOWN) {
            view.setMarkupLabel(lang.textMarkdownSupport(), configStorage.getConfigData().markupHelpLinkMarkdown);
        } else {
            view.setMarkupLabel(lang.textJiraWikiMarkupSupport(), configStorage.getConfigData().markupHelpLinkJiraMarkup);
        }

        view.privateComment().setValue(false);
        view.getPrivacyVisibility().setVisible(isPrivateVisible);

        view.setCaseCreatorId(event.caseCreatorId);

        reloadComments(caseType, caseId);
    }

    @Event
    public void onReload(CaseCommentEvents.Reload event) {
        reloadComments(caseType, caseId);
    }

    @Event
    public void onCaseCommentRemovedClientEvent( CaseCommentRemovedClientEvent event ) {
        if (!view.isAttached()) return;
        if (!Objects.equals( caseId, event.getCaseObjectId() )) return;
        if (Objects.equals( policyService.getProfileId(), event.getPersonId() )) return;

        AbstractCaseCommentItemView oldView = findItemViewByCommentId( event.getCaseCommentID() );
        if (oldView != null) {
            Collection<Attachment> commentAttachments = oldView.attachmentContainer().getAll();
            if (CollectionUtils.isNotEmpty(commentAttachments)) {
                fireEvent(new AttachmentEvents.Remove(caseId, commentAttachments));
            }
            view.removeComment( oldView );
            itemViewToModel.remove( oldView );
        }
    }

    @Event
    public void onCaseCommentSavedClientEvent( CaseCommentSavedClientEvent event ) {
        if (!view.isAttached()) return;
        if (!Objects.equals( caseId, event.getCaseObjectId() )) return;
        if (Objects.equals( policyService.getProfileId(), event.getPersonId() )) return;

        caseCommentController.getCaseComment( event.getCaseCommentID(), new FluentCallback<CaseComment>()
                .withSuccess( comment -> {
                    if (comment == null) return;
                    if (!view.isAttached()) return;

                    renderTextAsync(comment.getText(), textMarkup, converted -> {
                        comment.setText(converted);
                        AbstractCaseCommentItemView newView = makeCommentView( comment );
                        AbstractCaseCommentItemView oldView = findItemViewByCommentId( comment.getId() );
                        if (oldView != null) {
                            view.replaceCommentView( oldView, newView );
                            newView.displayUpdatedAnimation();
                            itemViewToModel.remove( oldView );
                            itemViewToModel.put( newView, comment );
                            if (CollectionUtils.isEmpty(oldView.attachmentContainer().getAll())){
                                updateCaseAttachment(Collections.emptyList(), comment.getCaseAttachments());
                            } else {
                                updateCaseAttachment(new ArrayList<>(oldView.attachmentContainer().getAll()), comment.getCaseAttachments());
                            }
                            return;
                        }

                        itemViewToModel.put( newView, comment );
                        view.addCommentToFront( newView );
                        newView.displayAddedAnimation();
                        updateCaseAttachment(Collections.emptyList(), comment.getCaseAttachments());
                    });
                } ) );
    }

    @Event
    public void onDisableNewComment(CaseCommentEvents.DisableNewComment event) {
        this.isNewCommentEnabled = false;
        view.setNewCommentDisabled(true);
    }

    @Override
    public void onRemoveClicked(final AbstractCaseCommentItemView itemView ) {
        CaseComment caseComment = itemViewToModel.get( itemView );

        String validationString = makeAllowRemoveValidationString(caseComment, profile);
        if (validationString != null) {
            fireEvent(new NotifyEvents.Show(validationString, NotifyEvents.NotifyType.ERROR));
            return;
        }

        lastCommentView = null;

        if (caseComment.getCaseStateId() != null) {
            caseComment.setText(null);
            caseCommentController.saveCaseComment(caseType, caseComment, new FluentCallback<CaseComment>()
                    .withSuccess(comment -> itemView.setMessage(null))
            );
            return;
        }

        caseCommentController.removeCaseComment(caseType, caseComment, new FluentCallback<Long>()
                .withSuccess(result -> {
                    Collection<Attachment> commentAttachments = itemView.attachmentContainer().getAll();
                    if (CollectionUtils.isNotEmpty(commentAttachments)) {
                        fireEvent(new AttachmentEvents.Remove(caseId, commentAttachments));
                    }
                    view.removeComment(itemView);
                    itemViewToModel.remove(itemView);
                    updateTimeElapsedInIssue(itemViewToModel.values());
                })
        );
    }

    @Override
    public void onEditClicked( AbstractCaseCommentItemView itemView ) {
        CaseComment caseComment = itemViewToModel.get( itemView );

        String validationString = makeAllowEditValidationString(caseComment, profile);
        if (validationString != null) {
            fireEvent(new NotifyEvents.Show(validationString, NotifyEvents.NotifyType.ERROR));
            return;
        }

        this.comment = caseComment;
        this.lastCommentView = itemView;

        view.attachmentContainer().clear();
        tempAttachments.clear();

        Collection<Attachment> commentAttachments = itemView.attachmentContainer().getAll();
        if(!commentAttachments.isEmpty()) {
            view.attachmentContainer().add(commentAttachments);
            tempAttachments.addAll(commentAttachments);
        }

        view.message().setValue(comment.getText(), true);

        if (isElapsedTimeEnabled && comment.getTimeElapsed() != null) {
            view.timeElapsed().setTime(comment.getTimeElapsed());
            view.timeElapsedType().setValue(comment.getTimeElapsedType());
        }

        view.getPrivacyVisibility().setVisible(false);

        view.focus();
    }

    @Override
    public void onReplyClicked( AbstractCaseCommentItemView itemView ) {
        CaseComment value = itemViewToModel.get( itemView );
        if ( value == null ) {
            return;
        }

        comment = null;

        accountService.getLoginByPersonId(value.getAuthorId(), new FluentCallback<String>()
                .withSuccess(login -> view.message().setValue(appendLogin(view.message().getValue(), login), true))
        );

        view.focus();
    }

    @Override
    public void onSendClicked() {
        if (!isNewCommentEnabled) {
            return;
        }
        send();
    }

    @Override
    public void onEditLastMessage() {
        CaseComment value = itemViewToModel.get( lastCommentView );
        if ( value == null ) {
            return;
        }

        view.message().setValue( value.getText(), true );
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
    public void onRemoveAttachment(CaseCommentItemView itemView, Attachment attachment) {
        if(comment != null && comment == itemViewToModel.get( itemView )) {
            //deleting while editing
            fireEvent(new NotifyEvents.Show(lang.errEditIssueComment(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        removeAttachment(attachment.getId(), () -> {
            fireEvent(new AttachmentEvents.Remove(caseId, Collections.singletonList(attachment)));

            itemView.attachmentContainer().remove(attachment);
            if(itemView.attachmentContainer().getAll().isEmpty()){
                itemView.showAttachments(false);
            }

            reloadComments(caseType, caseId);
        });
    }

    @Override
    public void onTimeElapsedTypeClicked(AbstractCaseCommentItemView itemView) {
        CaseComment caseComment = itemViewToModel.get(itemView);

        if (caseComment == null) {
            return;
        }

        if (!Objects.equals(caseComment.getAuthorId(), policyService.getProfileId())) {
            return;
        }

        itemView.timeElapsedTypePopupVisibility().setVisible(!itemView.timeElapsedTypePopupVisibility().isVisible());
    }

    @Override
    public void onDisplayPreviewChanged( Boolean isDisplayPreview ) {
        storage.set( IS_PREVIEW_DISPLAYED, String.valueOf( isDisplayPreview ) );
        fireChangedPreview();
    }

    private void removeAttachment(Long id, Runnable successAction){
        attachmentService.removeAttachmentEverywhere(caseType, id, new FluentCallback<Long>()
                .withError((throwable, defaultErrorHandler, status) -> {
                    if (En_ResultStatus.NOT_FOUND.equals(status)) {
                        fireEvent(new NotifyEvents.Show(lang.fileNotFoundError(), NotifyEvents.NotifyType.ERROR));
                        return;
                    }

                    if (En_ResultStatus.NOT_REMOVED.equals(status)) {
                        fireEvent(new NotifyEvents.Show(lang.removeFileError(), NotifyEvents.NotifyType.ERROR));
                        return;
                    }

                    defaultErrorHandler.accept(throwable);
                })
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

    private void fillView(List<CaseComment> comments){
        itemViewToModel.clear();
        view.clearCommentsContainer();
        view.setNewCommentHidden(!isModifyEnabled);
        view.setNewCommentDisabled(!isNewCommentEnabled);

        view.setCommentPlaceholder(lang.commentAddMessageMentionPlaceholder());

        List<AbstractCaseCommentItemView> views = new ArrayList<>();
        List<String> textList = new ArrayList<>();

        for (CaseComment comment : comments) {
            AbstractCaseCommentItemView itemView = makeCommentView(comment);
            if (StringUtils.isNotEmpty(comment.getText())) {
                views.add(itemView);
                textList.add(comment.getText());
            }
            itemViewToModel.put( itemView, comment );
            view.addCommentToFront( itemView.asWidget() );
        }

        textRenderController.render(textMarkup, textList, true, new FluentCallback<List<String>>()
                .withSuccess(converted -> {
                    for (int i = 0; i < converted.size(); i++) {
                        views.get(i).setMessage(converted.get(i));
                    }
                    views.clear();
                    textList.clear();
                })
        );
    }

    private AbstractCaseCommentItemView makeCommentView(CaseComment value) {
        AbstractCaseCommentItemView itemView = issueProvider.get();
        itemView.setActivity(this);

        if (value.getAuthorId().equals(profile.getId())) {
            itemView.setMine();
            itemView.setIcon(AvatarUtils.getAvatarUrl(profile));
        } else {
            itemView.setIcon(AvatarUtils.getAvatarUrl(value.getAuthor()));
        }

        itemView.setDate(DateFormatter.formatDateTime(value.getCreated()));
        itemView.setOwner(getOwnerName(value));

        CaseLink remoteLink = value.getRemoteLink();
        if ( remoteLink != null ) {
            itemView.setRemoteLinkNumber(remoteLink.getRemoteId());
            itemView.setRemoteLinkHref(caseLinkProvider.getLink(remoteLink.getType(), remoteLink.getRemoteId()));
        }

        if (StringUtils.isNotEmpty(value.getText())) {
            itemView.setMessage(value.getText());
        }
        itemView.clearElapsedTime();
        fillTimeElapsed(value, itemView);
        if (isPrivateVisible) {
            itemView.setPrivacyFlag(value.isPrivateComment());
        }

        boolean isStateChangeComment = value.getCaseStateId() != null;
        boolean isImportanceChangeComment = value.getCaseImpLevel() != null;
        boolean isManagerChangeComment = value.getCaseManagerId() != null;
        boolean isChangeComment = isStateChangeComment || isImportanceChangeComment || isManagerChangeComment;

        if (HelperFunc.isEmpty( value.getText() ) && isChangeComment) {
            itemView.hideOptions();
        }

        if ( isStateChangeComment ) {
            itemView.setStatus( value.getCaseStateName() );
        }

        if ( isImportanceChangeComment ) {
            itemView.setImportanceLevel( value.getCaseImportance() );
        }

        if ( isManagerChangeComment ) {
            itemView.setManagerInfo(makeManagerInfo(value.getCaseManagerShortName(), value.getManagerCompanyName()));
        }

        bindAttachmentsToComment(itemView, value.getCaseAttachments());

        itemView.setTimeElapsedTypeChangeHandler(event -> updateTimeElapsedType(event.getValue(), value, itemView));

        itemView.enabledEdit(isModifyEnabled && (makeAllowEditValidationString( value, profile) == null));
        itemView.enableReply(isModifyEnabled);

        return itemView;
    }

    private String makeManagerInfo(String managerShortName, String managerCompanyName) {
        return transliteration(managerShortName + " (" + managerCompanyName + ")");
    }

    private void updateTimeElapsedType(En_TimeElapsedType type, CaseComment value, AbstractCaseCommentItemView itemView) {
        value.setTimeElapsedType(type);
        caseCommentController.updateCaseTimeElapsedType(value.getId(), type, new FluentCallback<Boolean>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errEditTimeElapsedType(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(updated -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fillTimeElapsed(value, itemView);
                })
        );
    }

    private void fillTimeElapsed( CaseComment value, AbstractCaseCommentItemView itemView ) {
        if (isElapsedTimeEnabled && value.getTimeElapsed() != null) {
            String timeType = (value.getTimeElapsedType() == null || value.getTimeElapsedType().equals( En_TimeElapsedType.NONE ) ? "" : ", " + timeElapsedTypeLang.getName( value.getTimeElapsedType() ));
            itemView.setTimeElapsed( StringUtils.join(
                    " ( +", workTimeFormatter.asString( value.getTimeElapsed() ), timeType, " )"
                    ).toString()
            );
        }

        itemView.setTimeElapsedType(value.getTimeElapsedType());
    }

    private void bindAttachmentsToComment(AbstractCaseCommentItemView itemView, List<CaseAttachment> caseAttachments){
        itemView.attachmentContainer().clear();

        if(caseAttachments == null || caseAttachments.isEmpty()){
            itemView.showAttachments(false);
        }else {
            itemView.showAttachments(true);
            requestAttachments(extractIds(caseAttachments), itemView.attachmentContainer()::add);
        }
    }

    private void requestAttachments(List<Long> ids, Consumer<Collection<Attachment>> addAction){

        attachmentService.getAttachments(caseType, ids, new RequestCallback<List<Attachment>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent( new NotifyEvents.Show( lang.attachmentsNotLoaded(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess(List<Attachment> list) {
                if(list == null){
                    onError(null);
                    return;
                }
                addAction.accept(list);
            }
        });
    }

    private void synchronizeAttachments(Collection<Attachment> oldAttachments, Collection<Attachment> newAttachments){
        ArrayList<Attachment> listForRemove = new ArrayList<>(oldAttachments);
        ArrayList<Attachment> listForAdd = new ArrayList<>(newAttachments);
        listForRemove.removeIf(listForAdd::remove);

        if(!listForRemove.isEmpty())
            fireEvent(new AttachmentEvents.Remove(caseId, listForRemove));
        if(!listForAdd.isEmpty())
            fireEvent(new AttachmentEvents.Add(caseId, listForAdd));
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
                .withError(t -> {
                    unlockSave();
                    defaultErrorHandler.accept(t);
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
        if (isNew) {
            comment.setPrivateComment(isPrivateCase || view.privateComment().getValue());
        }
        comment.setCaseAttachments(tempAttachments.stream()
                .map(a -> new CaseAttachment(caseId, a.getId(), commentId))
                .collect(Collectors.toList())
        );
        return comment;
    }

    private void onCommentSent(boolean isEdit, CaseComment caseComment) {

        storage.remove(makeStorageKey(caseComment.getCaseId()));

        caseComment.setCaseAttachments(comment.getCaseAttachments());
        stream(tempAttachments).forEach(attachment -> attachment.setPrivate(comment.isPrivateComment()));

        if (isEdit) {
            renderTextAsync(caseComment.getText(), textMarkup, lastCommentView::setMessage);
            lastCommentView.clearElapsedTime();
            fillTimeElapsed( comment, lastCommentView );

            Collection<Attachment> prevAttachments = lastCommentView.attachmentContainer().getAll();

            if (!(prevAttachments.isEmpty() && tempAttachments.isEmpty())) {
                synchronizeAttachments(prevAttachments, tempAttachments);
                lastCommentView.attachmentContainer().clear();
                lastCommentView.attachmentContainer().add(tempAttachments);
                lastCommentView.showAttachments(!tempAttachments.isEmpty());
            }
        } else {
            fireEvent(new AttachmentEvents.Add(caseId, tempAttachments));
            AbstractCaseCommentItemView itemView = makeCommentView(caseComment);
            lastCommentView = itemView;
            itemViewToModel.put( itemView, caseComment );
            view.addCommentToFront(itemView.asWidget());
            renderTextAsync(caseComment.getText(), textMarkup, itemView::setMessage);
        }

        comment = null;
        view.message().setValue(null, true);
        view.attachmentContainer().clear();
        view.clearTimeElapsed();
        tempAttachments.clear();
        view.getPrivacyVisibility().setVisible(isPrivateVisible);
        updateTimeElapsedInIssue(itemViewToModel.values());
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


    private void updateTimeElapsedInIssue(Collection<CaseComment> comments) {
        Long timeElapsed = stream(comments).filter(cmnt -> cmnt.getTimeElapsed() != null)
                .mapToLong(cmnt -> cmnt.getTimeElapsed()).sum();
        fireEvent( new IssueEvents.ChangeTimeElapsed(timeElapsed) );
    }

    private String getOwnerName(CaseComment caseComment) {
        if (!StringUtils.isEmpty(caseComment.getOriginalAuthorName()))
            return transliteration(caseComment.getOriginalAuthorName());
        if (caseComment.getAuthor() != null)
            return transliteration(caseComment.getAuthor().getDisplayName());
        return "Unknown";
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

    private String makeCommentText(String commentText){
        String text = storage.get(makeStorageKey(caseId));
        return isEmpty(text) ? commentText : text;
    }

    private String makeStorageKey(Long id){
        return STORAGE_CASE_COMMENT_PREFIX + id;
    }

    private void renderTextAsync(String text, En_TextMarkup textMarkup, Consumer<String> consumer) {
        textRenderController.render(text, textMarkup, true, new FluentCallback<String>()
                .withError(throwable -> consumer.accept(text))
                .withSuccess(consumer));
    }

    private void reloadComments(En_CaseType caseType, Long caseId) {
        caseCommentController.getCaseComments(caseType, caseId, new FluentCallback<List<CaseComment>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errNotFound(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(this::fillView)
        );
    }

    private String transliteration(String input) {
        return TransliterationUtils.transliterate(input, LocaleInfo.getCurrentLocale().getLocaleName());
    }

    private AbstractCaseCommentItemView findItemViewByCommentId( Long commentId ) {
        if (commentId == null) return null;
        for (Map.Entry<AbstractCaseCommentItemView, CaseComment> entry : itemViewToModel.entrySet()) {
            if (entry.getValue() == null) continue;
            if (!Objects.equals( entry.getValue().getId(), commentId )) continue;
            return entry.getKey();
        }

        return null;
    }

    private void updateCaseAttachment (List<Attachment> currentAttachments, List<CaseAttachment> newCommentAttachments){
        if (CollectionUtils.isNotEmpty(newCommentAttachments)) {
            requestAttachments(extractIds(newCommentAttachments), attachmentsFromDb -> {
                List<Attachment> attachmentsToAdd = makeListAttachmentsToAdd(currentAttachments, new ArrayList<>(attachmentsFromDb));
                List<Attachment> attachmentsToRemove = makeListAttachmentsToRemove(currentAttachments, new ArrayList<>(attachmentsFromDb));

                if (!attachmentsToAdd.isEmpty()) {
                    fireEvent(new AttachmentEvents.Add(caseId, attachmentsToAdd));
                }

                if (!attachmentsToRemove.isEmpty()) {
                    fireEvent(new AttachmentEvents.Remove(caseId, attachmentsToRemove));
                }
            });
        } else {
            if (CollectionUtils.isNotEmpty(currentAttachments)){
                fireEvent(new AttachmentEvents.Remove(caseId, currentAttachments));
            }
        }
    }

    private List<Attachment> makeListAttachmentsToRemove(List<Attachment> currentAttachments, List<Attachment> newAttachments) {
        List<Attachment> removeList = new ArrayList<>(currentAttachments);
        removeList.removeIf(currentAttachment -> newAttachments.contains(currentAttachment));
        return removeList;
    }

    private List<Attachment> makeListAttachmentsToAdd(List<Attachment> currentAttachments, List<Attachment> newAttachments) {
        List<Attachment> addList = new ArrayList<>(newAttachments);
        addList.removeIf(newAttachment -> currentAttachments.contains(newAttachment));
        return addList;
    }

    private final Timer changedPreviewTimer = new Timer() {
        @Override
        public void run() {
            fireChangedPreview();
        }
    };

    @Inject
    Lang lang;
    @Inject
    TimeElapsedTypeLang timeElapsedTypeLang;
    @Inject
    CaseCommentControllerAsync caseCommentController;
    @Inject
    AbstractCaseCommentListView view;
    @Inject
    Provider<AbstractCaseCommentItemView> issueProvider;
    @Inject
    AttachmentControllerAsync attachmentService;
    @Inject
    TextRenderControllerAsync textRenderController;
    @Inject
    CaseLinkProvider caseLinkProvider;
    @Inject
    private LocalStorageService storage;
    @Inject
    AccountControllerAsync accountService;

    @Inject
    ConfigStorage configStorage;
    @Inject
    PolicyService policyService;

    private CaseComment comment;
    private AbstractCaseCommentItemView lastCommentView;
    private WorkTimeFormatter workTimeFormatter;

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

    private Map<AbstractCaseCommentItemView, CaseComment> itemViewToModel = new HashMap<>();
    private Collection<Attachment> tempAttachments = new ArrayList<>();

    @Inject
    private DefaultErrorHandler defaultErrorHandler;
    private final static int PREVIEW_CHANGE_DELAY_MS = 200;

    private final String STORAGE_CASE_COMMENT_PREFIX = "CaseСomment_";
    private final String IS_PREVIEW_DISPLAYED = STORAGE_CASE_COMMENT_PREFIX+"is_preview_displayed";

    private static final Logger log = Logger.getLogger( CaseCommentListActivity.class.getName() );
}
