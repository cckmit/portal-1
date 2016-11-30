package ru.protei.portal.ui.issue.client.view.comment.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.issue.client.activity.comment.item.AbstractIssueCommentItemActivity;
import ru.protei.portal.ui.issue.client.activity.comment.item.AbstractIssueCommentItemView;

/**
 * Один комментарий
 */
public class IssueCommentItemView
        extends Composite
        implements AbstractIssueCommentItemView {


    public IssueCommentItemView() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity( AbstractIssueCommentItemActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setValue( CaseComment value ) {
        text.setText( value.getText() );
        date.setText( DateFormatter.formatDateTime( value.getCreated() ) );
        owner.setText( String.valueOf( value.getAuthorId() ) );
    }

    @UiField
    Label date;
    @UiField
    Label text;
    @UiField
    Anchor owner;

    private AbstractIssueCommentItemActivity activity;

    interface IssueCommentUiBinder extends UiBinder<Widget, IssueCommentItemView> {}
    private static IssueCommentUiBinder ourUiBinder = GWT.create( IssueCommentUiBinder.class );
}