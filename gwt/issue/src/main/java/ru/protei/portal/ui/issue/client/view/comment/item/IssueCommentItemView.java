package ru.protei.portal.ui.issue.client.view.comment.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.issue.client.activity.comment.item.AbstractIssueCommentItemActivity;
import ru.protei.portal.ui.issue.client.activity.comment.item.AbstractIssueCommentItemView;

/**
 * Один комментарий
 */
public class IssueCommentItemView
        extends Composite
        implements AbstractIssueCommentItemView {

    @Inject
    public void onInit() {
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
        this.owner.setInnerText( value );
    }

    @Override
    public void setMessage( String value ) {
        this.message.getElement().setInnerHTML( value );
        this.messageBlock.removeClassName( "hide" );
    }

    @Override
    public void setMine() {
        root.setStyleName( "right" );
    }

    @Override
    public void setStatus( En_CaseState value ) {
        status.setClassName( "case-" + value.name().toLowerCase() );
        this.status.setInnerText( stateLang.getStateName( value ) );
        this.info.removeClassName( "hide" );
    }

    @Override
    public void enabledEdit( boolean isEnabled ) {
        remove.setVisible( isEnabled );
        edit.setVisible( isEnabled );
    }

    @Override
    public void showAttachments(boolean isShow){
        if(isShow)
            attachBlock.removeClassName("hide");
        else
            attachBlock.addClassName("hide");
    }

    @Override
    public HasAttachments attachmentContainer(){
        return attachList;
    }

    @UiHandler( "remove" )
    public void onRemoveClicked( ClickEvent event ) {
        event.preventDefault();
        if ( activity != null ) {
            activity.onRemoveClicked( this );
        }
    }

    @UiHandler( "edit" )
    public void onEditClicked( ClickEvent event ) {
        event.preventDefault();
        if ( activity != null ) {
            activity.onEditClicked( this );
        }
    }

    @UiHandler( "reply" )
    public void onReplyClicked( ClickEvent event ) {
        event.preventDefault();
        if ( activity != null ) {
            activity.onReplyClicked( this );
        }
    }

    @UiHandler("attachList")
    public void onRemoveAttachment(RemoveEvent event){
        activity.onRemoveAttachment(this, event.getAttachment());
    }

    @UiField
    InlineLabel message;
    @UiField
    Anchor remove;
    @UiField
    Anchor edit;
    @UiField
    Anchor reply;
    @UiField
    HTMLPanel root;
    @Inject
    @UiField(provided = true)
    AttachmentList attachList;
    @UiField
    DivElement attachBlock;
    @UiField
    DivElement messageBlock;
    @UiField
    LIElement date;
    @UiField
    LIElement owner;
    @UiField
    LIElement info;
    @UiField
    LIElement status;
    @Inject
    En_CaseStateLang stateLang;

    private AbstractIssueCommentItemActivity activity;

    interface IssueCommentUiBinder extends UiBinder<Widget, IssueCommentItemView> {}
    private static IssueCommentUiBinder ourUiBinder = GWT.create( IssueCommentUiBinder.class );
}