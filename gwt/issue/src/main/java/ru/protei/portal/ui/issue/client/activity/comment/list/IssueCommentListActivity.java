package ru.protei.portal.ui.issue.client.activity.comment.list;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.ui.common.client.common.AttachmentCollection;
import ru.protei.portal.ui.common.client.common.AttachmentCollectionImpl;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AttachmentServiceAsync;
import ru.protei.portal.ui.common.client.service.IssueServiceAsync;
import ru.protei.portal.ui.common.client.widget.uploader.FileUploader;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.issue.client.activity.comment.item.AbstractIssueCommentItemActivity;
import ru.protei.portal.ui.issue.client.activity.comment.item.AbstractIssueCommentItemView;
import ru.protei.portal.ui.issue.client.util.IssueCommentUtils;
import ru.protei.portal.ui.issue.client.view.comment.item.IssueCommentItemView;

import java.util.*;
import java.util.function.Consumer;

/**
 * Активность списка комментариев
 */
public abstract class IssueCommentListActivity
        implements Activity,
        AbstractIssueCommentListActivity, AbstractIssueCommentItemActivity {

    @Inject
    public void onInit() {
        view.setActivity( this );
        view.setFileUploadHandler(new FileUploader.FileUploadHandler() {
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
        if(event.attachmentCollection != null)
            attachmentCollection = event.attachmentCollection;
        else
            attachmentCollection = new AttachmentCollectionImpl();

        view.message().setValue( null );
        view.attachmentContainer().clear();
        view.getCommentsContainer().clear();

        requestData( event.caseId );
    }

    @Override
    public void onRemoveClicked( AbstractIssueCommentItemView itemView ) {
        lastCommentView = null;
        CaseComment caseComment = itemViewToModel.get( itemView );

        if ( caseComment == null || !IssueCommentUtils.isEnableEdit( caseComment, profile.getId() ) ) {
            fireEvent( new NotifyEvents.Show( lang.errEditIssueCommentNotAllowed(), NotifyEvents.NotifyType.ERROR ) );
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
                    commentAttachments.forEach(attachmentCollection::removeAttachment);

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
            commentAttachments.forEach(view.attachmentContainer()::add);
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
        if ( comment == null ) {
            initCaseCommentByUser();
        }
        boolean isEdit = comment.getId() != null;

        String message = view.message().getValue();
        if ( message == null || message.isEmpty() ) {
            fireEvent( new NotifyEvents.Show( lang.errEditIssueCommentEmpty(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        comment.setText( IssueCommentUtils.prewrapMessage( message ) );
        comment.setAttachments(tempAttachments);

        issueService.editIssueComment( comment, new RequestCallback<CaseComment>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errEditIssueComment(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( CaseComment result ) {
                if(result == null){
                    onError(null);
                    return;
                }
                result.setAttachmentsIds(comment.getAttachmentsIds());

                if ( isEdit ) {
                    lastCommentView.setMessage( result.getText() );

                    Collection<Attachment> prevAttachments = lastCommentView.attachmentContainer().getAll();

                    if(!(prevAttachments.isEmpty() && tempAttachments.isEmpty())){
                        synchronizeAttachments(prevAttachments, tempAttachments);
                        lastCommentView.attachmentContainer().clear();
                        tempAttachments.forEach(lastCommentView.attachmentContainer()::add);
                        lastCommentView.showAttachments(!tempAttachments.isEmpty());
                    }
                } else {
                    tempAttachments.forEach(attachmentCollection::addAttachment);

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

        if(comment != null && comment.getAttachmentsIds()!= null && comment.getAttachmentsIds().contains(attachment.getId())){
            // editing comment
            removeTempAttachmentAction.run();
        }else
            removeAttachment(attachment.getId(), removeTempAttachmentAction);
    }

    @Override
    public void onRemoveAttachment(IssueCommentItemView itemView, Attachment attachment) {
        removeAttachment(attachment.getId(), () -> {
            attachmentCollection.removeAttachment(attachment);
            itemView.attachmentContainer().remove(attachment);
            if(itemView.attachmentContainer().getAll().size() == 0){
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

        for ( CaseComment value : comments ) {
            AbstractIssueCommentItemView itemView = makeCommentView( value );
            view.getCommentsContainer().add( itemView.asWidget() );
        }
    }

    private AbstractIssueCommentItemView makeCommentView( CaseComment value ) {
        AbstractIssueCommentItemView itemView = issueProvider.get();
        itemView.setActivity( this );
        itemView.setDate( DateFormatter.formatDateTime( value.getCreated() ) );
        itemView.setOwner( value.getAuthor() == null ? "Unknown" : value.getAuthor().getDisplayName() );

        if ( HelperFunc.isNotEmpty( value.getText() ) ) {
            itemView.setMessage( value.getText() );
        }

        if ( value.getCaseStateId() != null ) {
            En_CaseState caseState = En_CaseState.getById( value.getCaseStateId() );
            itemView.setStatus( caseState );
        }

        bindAttachmentsToComment( itemView, value.getAttachmentsIds() );

        itemView.enabledEdit( IssueCommentUtils.isEnableEdit( value, profile.getId() ) );
        if ( value.getAuthorId().equals( profile.getId() ) ) {
            itemView.setMine();
        }
        itemViewToModel.put( itemView, value );

        return itemView;
    }

    private void bindAttachmentsToComment(AbstractIssueCommentItemView itemView, List<Long> attachmentsIds){
        itemView.attachmentContainer().clear();

        if(attachmentsIds == null || attachmentsIds.isEmpty()){
            itemView.showAttachments(false);
            return;
        }
        itemView.showAttachments(true);


        List<Long> undefinedAttachmentIds = null;
        for(Long attachId: attachmentsIds){
            if(attachmentCollection.containsKey(attachId)) {
                itemView.attachmentContainer().add(
                        attachmentCollection.get(attachId)
                );
            }else {
                if(undefinedAttachmentIds == null)
                    undefinedAttachmentIds = new ArrayList<>();

                undefinedAttachmentIds.add(attachId);
            }
        }

        if(undefinedAttachmentIds != null){
            requestAttachments(undefinedAttachmentIds, itemView.attachmentContainer()::add);
        }
    }

    private void initCaseCommentByUser() {
        comment = new CaseComment();
        comment.setAuthorId( profile.getId() );
        comment.setCaseId( show.caseId );
    }

    private void requestAttachments(List<Long> ids, Consumer<Attachment> addAction){

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
                list.forEach(addAction);
            }
        });
    }

    private void synchronizeAttachments(Collection<Attachment> oldAttachments, Collection<Attachment> newAttachments){
        if(oldAttachments.size() == newAttachments.size() && oldAttachments.containsAll(newAttachments))
            return;

        for(Attachment attach: oldAttachments)
            if(!newAttachments.contains(attach))
                attachmentCollection.removeAttachment(attach);

        for(Attachment attach: newAttachments)
            if(!oldAttachments.contains(attach))
                attachmentCollection.addAttachment(attach);
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

    private CaseComment comment;
    private AbstractIssueCommentItemView lastCommentView;

    private Profile profile;
    private IssueEvents.ShowComments show;
    private Map<AbstractIssueCommentItemView, CaseComment> itemViewToModel = new HashMap<>();
    private Collection<Attachment> tempAttachments = new ArrayList<>();

    private AttachmentCollection attachmentCollection;
}
