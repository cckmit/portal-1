package ru.protei.portal.ui.common.client.activity.casecomment.list;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseAttachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.activity.casecomment.item.AbstractCaseCommentItemActivity;
import ru.protei.portal.ui.common.client.activity.casecomment.item.AbstractCaseCommentItemView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.UserIconUtils;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AttachmentServiceAsync;
import ru.protei.portal.ui.common.client.service.CaseCommentControllerAsync;
import ru.protei.portal.ui.common.client.util.CaseCommentUtils;
import ru.protei.portal.ui.common.client.view.casecomment.item.CaseCommentItemView;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.StringUtils.isBlank;

/**
 * Активность списка комментариев
 */
public abstract class CaseCommentListActivity
        implements Activity,
        AbstractCaseCommentListActivity, AbstractCaseCommentItemActivity {

    @Inject
    public void onInit() {
        view.setActivity( this );
        view.setFileUploadHandler(new AttachmentUploader.FileUploadHandler() {
            @Override
            public void onSuccess(Attachment attachment) {
                addTempAttachment(attachment);
            }
            @Override
            public void onError() {
                fireEvent(new NotifyEvents.Show(lang.uploadFileError(), NotifyEvents.NotifyType.ERROR));
            }
        });
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
        this.isEditingEnabled = true;

        comment = null;
        lastCommentView = null;
        requesting = false;
        tempAttachments.clear();

        view.sendEnabled().setEnabled(true);
        view.message().setValue(null);
        view.attachmentContainer().clear();
        view.clearCommentsContainer();
        view.clearTimeElapsed();
        view.timeElapsedVisibility().setVisible(event.isElapsedTimeEnabled);
        view.setUserIcon(UserIconUtils.getGenderIcon(profile.getGender()));
        view.enabledNewComment(isEditingEnabled);
        view.setEnabledAttachAndComment(isEditingEnabled);

        caseCommentController.getCaseComments(caseType, caseId, new FluentCallback<List<CaseComment>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errNotFound(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(this::fillView)
        );
    }

    @Event
    public void onSaveComment(IssueEvents.SaveComment event) {
        if (!HelperFunc.isEmpty(view.message().getValue())) {
            send(event.id, event.handler);
        } else {
            event.handler.onSuccess();
        }
    }

    @Override
    public void onRemoveClicked(final AbstractCaseCommentItemView itemView ) {
        CaseComment caseComment = itemViewToModel.get( itemView );

        if(caseComment == comment) {
            //deleting while editing
            fireEvent(new NotifyEvents.Show(lang.errEditIssueComment(), NotifyEvents.NotifyType.INFO));
            return;
        }
        if ( caseComment == null || !CaseCommentUtils.isEnableEdit( caseComment, profile.getId() ) ) {
            fireEvent( new NotifyEvents.Show( lang.errEditIssueCommentNotAllowed(), NotifyEvents.NotifyType.ERROR ) );
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

        caseCommentController.removeCaseComment(caseType, caseComment, new FluentCallback<Void>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errRemoveIssueComment(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(v -> {
                    Collection<Attachment> commentAttachments = itemView.attachmentContainer().getAll();
                    if (CollectionUtils.isNotEmpty(commentAttachments)) {
                        fireEvent(new AttachmentEvents.Remove(caseId, commentAttachments));
                    }
                    view.removeComment(itemView);
                    itemViewToModel.remove(itemView);
                    fireEvent(new IssueEvents.ChangeModel());
                    updateTimeElapsedInIssue(itemViewToModel.values());
                })
        );
    }

    @Override
    public void onEditClicked( AbstractCaseCommentItemView itemView ) {
        CaseComment caseComment = itemViewToModel.get( itemView );

        if ( caseComment == null || !CaseCommentUtils.isEnableEdit( caseComment, profile.getId() ) ) {
            fireEvent( new NotifyEvents.Show( lang.errEditIssueCommentNotAllowed(), NotifyEvents.NotifyType.ERROR ) );
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

        String editedMessage = caseComment.getText();
        view.message().setValue( editedMessage );
        if (comment.getTimeElapsed() != null && policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW)) {
            view.timeElapsed().setTime(comment.getTimeElapsed());
        }
        view.focus();
    }

    @Override
    public void onReplyClicked( AbstractCaseCommentItemView itemView ) {
        CaseComment value = itemViewToModel.get( itemView );
        if ( value == null ) {
            return;
        }

        comment = null;

        String message = CaseCommentUtils.appendQuote(view.message().getValue(), value.getText());
        view.message().setValue( message );
        view.focus();
    }

    @Override
    public void onSendClicked() {
        send( null, null );
    }

    @Override
    public void onEditLastMessage() {
        CaseComment value = itemViewToModel.get( lastCommentView );
        if ( value == null ) {
            return;
        }

        view.message().setValue( value.getText() );
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
    public void onPreviewChanged(String text) {
        if (StringUtils.isBlank(text)) {
            view.setPreviewVisible(false);
        } else {
            view.setPreviewText(text);
            view.setPreviewVisible(true);
        }
    }

    @Override
    public void onRemoveAttachment(CaseCommentItemView itemView, Attachment attachment) {
        if(comment != null && comment == itemViewToModel.get( itemView )) {
            //deleting while editing
            fireEvent(new NotifyEvents.Show(lang.errEditIssueComment(), NotifyEvents.NotifyType.INFO));
            return;
        }

        removeAttachment(attachment.getId(), () -> {
            fireEvent(new AttachmentEvents.Remove(caseId, Collections.singletonList(attachment)));

            itemView.attachmentContainer().remove(attachment);
            if(itemView.attachmentContainer().getAll().isEmpty()){
                itemView.showAttachments(false);
            }
        });
    }

    private void removeAttachment(Long id, Runnable successAction){
        attachmentService.removeAttachmentEverywhere(id, new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.removeFileError(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean result) {
                if(!result) {
                    onError(null);
                    return;
                }
                successAction.run();
            }
        });
    }

    private void addTempAttachment(Attachment attach){
        view.attachmentContainer().add(attach);
        tempAttachments.add(attach);
    }

    private void fillView(List<CaseComment> comments){
        itemViewToModel.clear();
        view.clearCommentsContainer();
        view.enabledNewComment( policyService.hasEveryPrivilegeOf( En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT ));

        for (CaseComment value : comments) {
            AbstractCaseCommentItemView itemView = makeCommentView( value );
            view.addCommentToFront( itemView.asWidget() );
        }
    }

    private AbstractCaseCommentItemView makeCommentView(CaseComment value ) {
        AbstractCaseCommentItemView itemView = issueProvider.get();
        itemView.setActivity( this );

        if ( value.getAuthorId().equals( profile.getId() ) ) {
            itemView.setMine();
        }

        itemView.setDate( DateFormatter.formatDateTime( value.getCreated() ) );
        itemView.setOwner( getOwnerName(value) );
        itemView.setIcon( UserIconUtils.getGenderIcon(value.getAuthor().getGender() ) );
        itemView.setRemoteLink(value.getRemoteLink());

        itemView.clearElapsedTime();
        if (value.getTimeElapsed() != null && policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW) ) {
            itemView.timeElapsed().setTime(value.getTimeElapsed());
        }

        boolean isStateChangeComment = value.getCaseStateId() != null;
        boolean isImportanceChangeComment = value.getCaseImpLevel() != null;

        if ( HelperFunc.isNotEmpty( value.getText() ) ) {
            itemView.setMessage( value.getText() );
        }

        if ( HelperFunc.isEmpty( value.getText() ) && ( isStateChangeComment || isImportanceChangeComment)) {
            itemView.hideOptions();
        }

        itemView.enabledEdit( isEditingEnabled && policyService.hasEveryPrivilegeOf( En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT ) );

        if ( isStateChangeComment ) {
            En_CaseState caseState = En_CaseState.getById( value.getCaseStateId() );
            itemView.setStatus( caseState );
        }

        if ( isImportanceChangeComment ) {
            En_ImportanceLevel importance = En_ImportanceLevel.getById(value.getCaseImpLevel());
            itemView.setImportanceLevel(importance);
        }

        bindAttachmentsToComment(itemView, value.getCaseAttachments());

        itemView.enabledEdit( isEditingEnabled && CaseCommentUtils.isEnableEdit( value, profile.getId() ) );
        itemView.enableReply(isEditingEnabled);
        itemViewToModel.put( itemView, value );

        return itemView;
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

        attachmentService.getAttachments(ids, new RequestCallback<List<Attachment>>() {
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

    private void send(Long id, IssueEvents.SaveComment.SaveCommentCompleteHandler saveCommentCompleteHandler) {
        if ( requesting ) {
            return;
        }
        requesting = true;
        view.sendEnabled().setEnabled(false);

        if ( comment == null ) {
            comment = new CaseComment();
            comment.setAuthorId( profile.getId() );
        }

        boolean isEdit = comment.getId() != null;

        String message = view.message().getValue();
        if ( isBlank( message ) ) {
            if ( id == null ) {
                fireEvent(new NotifyEvents.Show(lang.errEditIssueCommentEmpty(), NotifyEvents.NotifyType.ERROR));
            }
            return;
        }

        comment.setCaseId( id != null ? id : caseId );
        comment.setText( message );
        comment.setTimeElapsed(view.timeElapsed().getTime());
        comment.setCaseAttachments(
                tempAttachments.stream()
                        .map(a -> new CaseAttachment(caseId, a.getId(), isEdit? comment.getId(): null))
                        .collect(Collectors.toList())
        );

        caseCommentController.saveCaseComment(caseType, comment, new FluentCallback<CaseComment>()
                .withError(throwable -> {
                    view.sendEnabled().setEnabled(true);
                    requesting = false;

                    if (saveCommentCompleteHandler != null) {
                        saveCommentCompleteHandler.onError(throwable);
                        return;
                    }

                    fireEvent(new NotifyEvents.Show(lang.errEditIssueComment(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(result -> {
                    requesting = false;
                    view.sendEnabled().setEnabled(true);

                    if (saveCommentCompleteHandler != null) {
                        saveCommentCompleteHandler.onSuccess();
                        return;
                    }
                    result.setCaseAttachments(comment.getCaseAttachments());

                    if (isEdit) {
                        lastCommentView.setMessage(result.getText());
                        lastCommentView.clearElapsedTime();
                        if (comment.getTimeElapsed() != null && policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW)) {
                            lastCommentView.timeElapsed().setTime(comment.getTimeElapsed());
                        }

                        Collection<Attachment> prevAttachments = lastCommentView.attachmentContainer().getAll();

                        if (!(prevAttachments.isEmpty() && tempAttachments.isEmpty())) {
                            synchronizeAttachments(prevAttachments, tempAttachments);
                            lastCommentView.attachmentContainer().clear();
                            lastCommentView.attachmentContainer().add(tempAttachments);
                            lastCommentView.showAttachments(!tempAttachments.isEmpty());
                        }
                    } else {
                        fireEvent(new AttachmentEvents.Add(caseId, tempAttachments));
                        AbstractCaseCommentItemView itemView = makeCommentView(result);
                        lastCommentView = itemView;
                        view.addCommentToFront(itemView.asWidget());
                    }

                    comment = null;
                    view.message().setValue(null);
                    view.attachmentContainer().clear();
                    view.clearTimeElapsed();
                    tempAttachments.clear();
                    fireEvent(new IssueEvents.ChangeModel());
                    updateTimeElapsedInIssue(itemViewToModel.values());
                })
        );
    }

    private void updateTimeElapsedInIssue(Collection<CaseComment> comments) {
        Long timeElapsed = CollectionUtils.stream(comments).filter(cmnt -> cmnt.getTimeElapsed() != null)
                .mapToLong(cmnt -> cmnt.getTimeElapsed()).sum();
        fireEvent( new IssueEvents.ChangeTimeElapsed(timeElapsed) );
    }

    private String getOwnerName(CaseComment caseComment) {
        if (!StringUtils.isEmpty(caseComment.getOriginalAuthorName()))
            return caseComment.getOriginalAuthorName();
        if (caseComment.getAuthor() != null)
            return caseComment.getAuthor().getDisplayName();
        return "Unknown";
    }

    @Inject
    Lang lang;
    @Inject
    CaseCommentControllerAsync caseCommentController;
    @Inject
    AbstractCaseCommentListView view;
    @Inject
    Provider<AbstractCaseCommentItemView> issueProvider;
    @Inject
    AttachmentServiceAsync attachmentService;
    @Inject
    PolicyService policyService;

    private CaseComment comment;
    private AbstractCaseCommentItemView lastCommentView;

    private Profile profile;

    private En_CaseType caseType;
    private boolean requesting = false;
    private boolean isEditingEnabled = true;
    private Long caseId;
    
    private Map<AbstractCaseCommentItemView, CaseComment> itemViewToModel = new HashMap<>();
    private Collection<Attachment> tempAttachments = new ArrayList<>();
}
