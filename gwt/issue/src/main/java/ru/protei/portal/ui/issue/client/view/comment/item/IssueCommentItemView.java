package ru.protei.portal.ui.issue.client.view.comment.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
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
    public void setDate( String value ) {
        this.date.setInnerText( value );
    }

    @Override
    public void setOwner( String value ) {
        this.owner.setText( value );
    }

    @Override
    public void setMessage( String value ) {
        this.message.setText( value );
    }

    @Override
    public void setMine() {
        root.setStyleName( "right" );
    }

    @Override
    public void enabledEdit( boolean isEnabled ) {
        remove.setVisible( isEnabled );
        edit.setVisible( isEnabled );
    }

    @UiHandler( "remove" )
    public void onRemoveClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onRemoveClicked( this );
        }
    }

    @UiHandler( "edit" )
    public void onEditClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onEditClicked( this );
        }
    }

    @UiHandler( "reply" )
    public void onReplyClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onReplyClicked( this );
        }
    }

    @UiField
    Label message;
    @UiField
    Anchor owner;
    @UiField
    SpanElement date;
    @UiField
    Anchor remove;
    @UiField
    Anchor edit;
    @UiField
    Anchor reply;
    @UiField
    HTMLPanel root;

    private AbstractIssueCommentItemActivity activity;

    interface IssueCommentUiBinder extends UiBinder<Widget, IssueCommentItemView> {}
    private static IssueCommentUiBinder ourUiBinder = GWT.create( IssueCommentUiBinder.class );
}