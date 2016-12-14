package ru.protei.portal.ui.issue.client.activity.comment.list;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueServiceAsync;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.issue.client.activity.comment.item.AbstractIssueCommentItemActivity;
import ru.protei.portal.ui.issue.client.activity.comment.item.AbstractIssueCommentItemView;
import ru.protei.portal.ui.issue.client.activity.comment.label.AbstractIssueCommentLabelView;
import ru.protei.portal.ui.issue.client.util.IssueCommentUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Активность списка комментариев
 */
public abstract class IssueCommentListActivity
        implements Activity,
        AbstractIssueCommentListActivity, AbstractIssueCommentItemActivity {


    @PostConstruct
    public void onInit() {
        view.setActivity( this );
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

        view.message().setValue( null );
        view.getCommentsContainer().clear();

        requestData( event.caseId );
    }

    @Override
    public void onRemoveClicked( AbstractIssueCommentItemView itemView ) {
        lastCommentView = null;
        CaseComment value = itemViewToModel.get( itemView );

        if ( value == null || !IssueCommentUtils.isEnableEdit( value, profile.getId() ) ) {
            fireEvent( new NotifyEvents.Show( lang.errEditIssueCommentNotAllowed(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        issueService.removeIssueComment( value, new RequestCallback<Void>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errRemoveIssueComment(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( Void result ) {
                view.getCommentsContainer().remove( itemView.asWidget() );
            }
        });
    }

    @Override
    public void onEditClicked( AbstractIssueCommentItemView itemView ) {
        CaseComment value = itemViewToModel.get( itemView );

        if ( value == null || !IssueCommentUtils.isEnableEdit( value, profile.getId() ) ) {
            fireEvent( new NotifyEvents.Show( lang.errEditIssueCommentNotAllowed(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        this.comment = value;
        this.lastCommentView = itemView;
        String editedMessage = value.getText();
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
        issueService.editIssueComment( comment, new RequestCallback<CaseComment>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errEditIssueComment(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( CaseComment value ) {
                comment = null;

                if ( isEdit ) {
                    lastCommentView.setMessage( value.getText() );
                } else {
                    AbstractIssueCommentItemView itemView = makeCommentView( value );
                    lastCommentView = itemView;
                    view.getCommentsContainer().add( itemView.asWidget() );
                }

                view.message().setValue( null );
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

    private void requestData( Long id ) {
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
            if ( value.getCaseStateId() != null ) {
                AbstractIssueCommentLabelView labelView = makeLabelView( value );
                view.getCommentsContainer().add( labelView.asWidget() );
            } else {
                AbstractIssueCommentItemView itemView = makeCommentView( value );
                view.getCommentsContainer().add( itemView.asWidget() );
            }
        }
    }

    private AbstractIssueCommentItemView makeCommentView( CaseComment value ) {
        AbstractIssueCommentItemView itemView = issueProvider.get();
        itemView.setActivity( this );
        itemView.setDate( DateFormatter.formatDateTime( value.getCreated() ) );
        itemView.setOwner( value.getAuthor() == null ? "Unknown" : value.getAuthor().getDisplayName() );
        itemView.setMessage( value.getText() );

        itemView.enabledEdit( IssueCommentUtils.isEnableEdit( value, profile.getId() ) );
        if ( value.getAuthorId().equals( profile.getId() ) ) {
            itemView.setMine();
        }
        itemViewToModel.put( itemView, value );

        return itemView;
    }

    private AbstractIssueCommentLabelView makeLabelView( CaseComment value ) {
        AbstractIssueCommentLabelView labelView = issueLabelProvider.get();
        En_CaseState caseState = En_CaseState.getById( value.getCaseStateId() );
        labelView.setDate( DateFormatter.formatDateTime( value.getCreated() ) );
        labelView.setOwner( value.getAuthor() == null ? "Unknown" : value.getAuthor().getDisplayName()  );
        labelView.setStatus( caseState );

        return labelView;
    }

    private void initCaseCommentByUser() {
        comment = new CaseComment();
        comment.setAuthorId( profile.getId() );
        comment.setCaseId( show.caseId );
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
    Provider<AbstractIssueCommentLabelView> issueLabelProvider;

    private CaseComment comment;
    private AbstractIssueCommentItemView lastCommentView;

    private Profile profile;
    private IssueEvents.ShowComments show;
    private Map<AbstractIssueCommentItemView, CaseComment> itemViewToModel = new HashMap<>();
}
