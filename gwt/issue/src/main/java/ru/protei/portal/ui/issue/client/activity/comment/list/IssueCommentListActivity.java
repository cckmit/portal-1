package ru.protei.portal.ui.issue.client.activity.comment.list;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseAttachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.UserIconUtils;
import ru.protei.portal.ui.common.client.events.AttachmentEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AttachmentServiceAsync;
import ru.protei.portal.ui.common.client.service.IssueServiceAsync;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.issue.client.activity.comment.item.AbstractIssueCommentItemActivity;
import ru.protei.portal.ui.issue.client.activity.comment.item.AbstractIssueCommentItemView;
import ru.protei.portal.ui.issue.client.util.IssueCommentUtils;
import ru.protei.portal.ui.issue.client.view.comment.item.IssueCommentItemView;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Активность списка комментариев
 */
public abstract class IssueCommentListActivity
        implements Activity,
        AbstractIssueCommentListActivity, AbstractIssueCommentItemActivity {

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
    public void onShow( IssueEvents.ShowComments event ) {
        this.show = event;

        event.parent.clear();
        event.parent.add(view.asWidget());
        view.message().setValue(null);
        view.attachmentContainer().clear();
        view.getCommentsContainer().clear();

        requestData( event.caseId );
    }

    @Event
    public  void onSaveComment( IssueEvents.SaveComment event ) {
        if(!HelperFunc.isEmpty(view.message().getValue()))
            send( event.id, true );
        else{
            issueSavedAlso(true);
        }
    }

    @Override
    public void onRemoveClicked( AbstractIssueCommentItemView itemView ) {
        CaseComment caseComment = itemViewToModel.get( itemView );

        if(caseComment == comment) {
            //deleting while editing
            fireEvent(new NotifyEvents.Show(lang.errEditIssueComment(), NotifyEvents.NotifyType.INFO));
            return;
        }
        if ( caseComment == null || !IssueCommentUtils.isEnableEdit( caseComment, profile.getId() ) ) {
            fireEvent( new NotifyEvents.Show( lang.errEditIssueCommentNotAllowed(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        lastCommentView = null;

        if (caseComment.getCaseStateId() != null) {
            caseComment.setText(null);
            issueService.editIssueComment(caseComment, new RequestCallback<CaseComment>() {
                @Override
                public void onError(Throwable throwable) {}

                @Override
                public void onSuccess(CaseComment caseComment) {
                    itemView.setMessage(null);
                }
            });
            return;
        }

        issueService.removeIssueComment( caseComment, new RequestCallback<Void>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errRemoveIssueComment(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( Void result ) {
                Collection<Attachment> commentAttachments = itemView.attachmentContainer().getAll();
                if(!commentAttachments.isEmpty())
                    fireEvent(new AttachmentEvents.Remove(show.caseId, commentAttachments));

                view.getCommentsContainer().remove( itemView.asWidget() );
                itemViewToModel.remove(itemView);
                fireEvent( new IssueEvents.ChangeModel() );
            }
        });
    }

    @Override
    public void onEditClicked( AbstractIssueCommentItemView itemView ) {
        CaseComment caseComment = itemViewToModel.get( itemView );

        if ( caseComment == null || !IssueCommentUtils.isEnableEdit( caseComment, profile.getId() ) ) {
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
        view.focus();
    }

    @Override
    public void onReplyClicked( AbstractIssueCommentItemView itemView ) {
        CaseComment value = itemViewToModel.get( itemView );
        if ( value == null ) {
            return;
        }

        this.comment = null;
        String quotedMessage = value.getText();
        view.message().setValue( IssueCommentUtils.quoteMessage( quotedMessage ) );
        view.focus();
    }

    @Override
    public void onSendClicked() {
        send( null, false );
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
    public void onRemoveAttachment(IssueCommentItemView itemView, Attachment attachment) {
        if(comment != null && comment == itemViewToModel.get( itemView )) {
            //deleting while editing
            fireEvent(new NotifyEvents.Show(lang.errEditIssueComment(), NotifyEvents.NotifyType.INFO));
            return;
        }

        removeAttachment(attachment.getId(), () -> {
            fireEvent(new AttachmentEvents.Remove(show.caseId, Collections.singletonList(attachment)));

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

    private void requestData(Long id ) {
        issueService.getIssueComments( id, new RequestCallback<List<CaseComment>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errNotFound(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List<CaseComment> comments ) {
                fillView( comments );
            }
        } );
    }

    private void fillView( List<CaseComment> comments ){
        itemViewToModel.clear();
        view.getCommentsContainer().clear();
        view.enabledNewComment( policyService.hasEveryPrivilegeOf( En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT ) );

        for ( CaseComment value : comments ) {
            AbstractIssueCommentItemView itemView = makeCommentView( value );
            view.getCommentsContainer().add( itemView.asWidget() );
        }
    }

    private AbstractIssueCommentItemView makeCommentView( CaseComment value ) {
        AbstractIssueCommentItemView itemView = issueProvider.get();
        itemView.setActivity( this );

        if ( value.getAuthorId().equals( profile.getId() ) ) {
            itemView.setMine();
        }

        itemView.setDate( DateFormatter.formatDateTime( value.getCreated() ) );
        itemView.setOwner( value.getAuthor() == null ? "Unknown" : value.getAuthor().getDisplayName() );
        itemView.setIcon( UserIconUtils.getGenderIcon(value.getAuthor().getGender() ) );
        if ( HelperFunc.isNotEmpty( value.getText() ) ) {
            itemView.setMessage( value.getText() );
        } else {
            itemView.hideOptions();
        }
        itemView.enabledEdit( policyService.hasEveryPrivilegeOf( En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT ) );

        if ( value.getCaseStateId() != null ) {
            En_CaseState caseState = En_CaseState.getById( value.getCaseStateId() );
            itemView.setStatus( caseState );
        }

        bindAttachmentsToComment(itemView, value.getCaseAttachments());

        itemView.enabledEdit( IssueCommentUtils.isEnableEdit( value, profile.getId() ) );
        itemViewToModel.put( itemView, value );


        return itemView;
    }

    private void bindAttachmentsToComment(AbstractIssueCommentItemView itemView, List<CaseAttachment> caseAttachments){
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
            fireEvent(new AttachmentEvents.Remove(show.caseId, listForRemove));
        if(!listForAdd.isEmpty())
            fireEvent(new AttachmentEvents.Add(show.caseId, listForAdd));
    }

    private List<Long> extractIds(Collection<CaseAttachment> list){
        return list == null || list.isEmpty()?
                Collections.emptyList():
                list.stream().map(CaseAttachment::getAttachmentId).collect(Collectors.toList());
    }

    private void send( Long id, boolean isIssueSavedAlso ) {
        if ( comment == null ) {
            comment = new CaseComment();
            comment.setAuthorId( profile.getId() );
        }

        boolean isEdit = comment.getId() != null;

        String message = view.message().getValue();
        if ( HelperFunc.isEmpty( message ) ) {
            if ( id == null ) {
                fireEvent(new NotifyEvents.Show(lang.errEditIssueCommentEmpty(), NotifyEvents.NotifyType.ERROR));
            }
            return;
        }

        comment.setCaseId( id != null ? id : show.caseId );
        comment.setText( message );
        comment.setCaseAttachments(
                tempAttachments.stream()
                        .map(a -> new CaseAttachment(show.caseId, a.getId(), isEdit? comment.getId(): null))
                        .collect(Collectors.toList())
        );

        issueService.editIssueComment( comment, new RequestCallback<CaseComment>() {
            @Override
            public void onError( Throwable throwable ) {
                if(isIssueSavedAlso)
                    issueSavedAlso(false);
                fireEvent( new NotifyEvents.Show( lang.errEditIssueComment(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( CaseComment result ) {
                if(isIssueSavedAlso){
                    issueSavedAlso(true);
                    return;
                }
                result.setCaseAttachments(comment.getCaseAttachments());

                if ( isEdit ) {
                    lastCommentView.setMessage( result.getText() );

                    Collection<Attachment> prevAttachments = lastCommentView.attachmentContainer().getAll();

                    if(!(prevAttachments.isEmpty() && tempAttachments.isEmpty())){
                        synchronizeAttachments(prevAttachments, tempAttachments);
                        lastCommentView.attachmentContainer().clear();
                        lastCommentView.attachmentContainer().add(tempAttachments);
                        lastCommentView.showAttachments(!tempAttachments.isEmpty());
                    }
                } else {
                    fireEvent(new AttachmentEvents.Add(show.caseId, tempAttachments));
                    AbstractIssueCommentItemView itemView = makeCommentView( result );
                    lastCommentView = itemView;
                    view.getCommentsContainer().add( itemView.asWidget() );
                }

                comment = null;
                view.message().setValue( null );
                view.attachmentContainer().clear();
                tempAttachments.clear();
                fireEvent( new IssueEvents.ChangeModel() );
            }
        } );
    };

    private void issueSavedAlso(boolean withComeback){
        fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
        fireEvent(new IssueEvents.ChangeModel());
        if(withComeback)
            fireEvent(new Back());
    }

    @Inject
    Lang lang;
    @Inject
    IssueServiceAsync issueService;
    @Inject
    AbstractIssueCommentListView view;
    @Inject
    Provider<AbstractIssueCommentItemView> issueProvider;
    @Inject
    AttachmentServiceAsync attachmentService;
    @Inject
    PolicyService policyService;

    private CaseComment comment;
    private AbstractIssueCommentItemView lastCommentView;

    private Profile profile;
    private IssueEvents.ShowComments show;
    private Map<AbstractIssueCommentItemView, CaseComment> itemViewToModel = new HashMap<>();
    private Collection<Attachment> tempAttachments = new ArrayList<>();
}
