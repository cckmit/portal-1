package ru.protei.portal.ui.issue.client.view.comment.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.issue.client.activity.comment.list.AbstractIssueCommentListActivity;
import ru.protei.portal.ui.issue.client.activity.comment.list.AbstractIssueCommentListView;

/**
 * Контейнер для комментариев
 */
public class IssueCommentListView
        extends Composite
        implements AbstractIssueCommentListView {


    @Inject
    public IssueCommentListView( Lang lang ) {
        initWidget( ourUiBinder.createAndBindUi( this ) );

        comment.getElement().setAttribute( "placeholder", lang.commentAddMessagePlaceholder() );
    }

    @Override
    public void setActivity( AbstractIssueCommentListActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getCommentsContainer() {
        return commentsContainer;
    }

    @Override
    public HasValue< String > message() {
        return comment;
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

    @UiField
    HTMLPanel root;
    @UiField
    TextArea comment;
    @UiField
    HTMLPanel commentsContainer;
    @UiField
    Button send;

    private AbstractIssueCommentListActivity activity;

    private static IssueListUiBinder ourUiBinder = GWT.create(IssueListUiBinder.class);
    interface IssueListUiBinder extends UiBinder<HTMLPanel, IssueCommentListView> {}
}