package ru.protei.portal.ui.issue.client.view.comment.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.ui.common.client.lang.En_CaseImportanceLang;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.issuelinks.list.IssueLinks;
import ru.protei.portal.ui.common.client.widget.markdown.Markdown;
import ru.protei.portal.ui.common.client.widget.timefield.HasTime;
import ru.protei.portal.ui.common.client.widget.timefield.TimeLabel;
import ru.protei.portal.ui.issue.client.activity.comment.item.AbstractIssueCommentItemActivity;
import ru.protei.portal.ui.issue.client.activity.comment.item.AbstractIssueCommentItemView;

import java.util.HashSet;
import java.util.Set;

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
        if ( root.getStyleName().contains( "right" ) ) {
            this.status.setInnerText( value );
            this.status.removeClassName( "status" );
            this.status.addClassName( "name" );
        } else {
            this.owner.setInnerText( value );
        }
    }

    @Override
    public void setMessage( String value ) {
        if ( value == null ) {
            this.message.getElement().setInnerText("");
            this.messageBlock.addClassName( "hide" );
            this.hideOptions();
            return;
        }

        this.message.getElement().setInnerHTML(Markdown.plain2escaped2markdown(value));
        this.messageBlock.removeClassName( "hide" );
    }

    @Override
    public void setMine() {
        root.setStyleName( "right" );
    }

    @Override
    public void setStatus( En_CaseState value ) {
        if ( root.getStyleName().contains( "right" ) ) {
            owner.removeClassName( "name" );
            owner.addClassName( "status" );
            owner.addClassName( "case-" + value.name().toLowerCase() );
            owner.setInnerText( stateLang.getStateName( value ) );
            info.setInnerText(lang.issueCommentChangeStatusTo());
            info.removeClassName( "hide" );
        } else {
            status.addClassName( "case-" + value.name().toLowerCase() );
            status.setInnerText( stateLang.getStateName( value ) );
            info.setInnerText(lang.issueCommentChangeStatusTo());
            info.removeClassName( "hide" );
        }
    }

    @Override
    public void setImportanceLevel(En_ImportanceLevel importance) {
        if (root.getStyleName().contains("right")) {
            owner.removeClassName("name");
            owner.addClassName("status");
            owner.addClassName("case-importance-" + importance.getCode().toLowerCase());
            owner.setInnerText(importanceLang.getImportanceName(importance));
            info.setInnerText(lang.issueCommentChangeImportanceTo());
            info.removeClassName("hide");
        } else {
            status.addClassName("case-importance-" + importance.getCode().toLowerCase());
            status.setInnerText(importanceLang.getImportanceName(importance));
            info.setInnerText(lang.issueCommentChangeImportanceTo());
            info.removeClassName("hide");
        }
    }

    @Override
    public void enabledEdit( boolean isEnabled ) {
        remove.setVisible( isEnabled );
        edit.setVisible( isEnabled );
    }

    @Override
    public void enableReply(boolean isEnabled) {
        reply.setVisible(isEnabled);
    }

    @Override
    public void showAttachments( boolean isShow ){
        if( isShow )
            attachBlock.removeClassName( "hide" );
        else
            attachBlock.addClassName( "hide" );
    }

    @Override
    public HasAttachments attachmentContainer(){
        return attachList;
    }

    @Override
    public void hideOptions() {
        options.removeFromParent();
    }

    @Override
    public void setIcon( String iconSrc ) {
        this.icon.setSrc( iconSrc );
    }

    @Override
    public HasTime timeElapsed() {
        return timeElapsed;
    }

    @Override
    public void clearElapsedTime() {
        timeElapsed.setText("");
    }

    @Override
    public void setRemoteLink(CaseLink remoteLink) {
        Set<CaseLink> set = new HashSet<>();
        if (remoteLink != null)
            set.add(remoteLink);
        this.remoteLink.setValue(set);
        this.remoteLink.setVisible(remoteLink != null);
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


    @Inject
    @UiField(provided = true)
    IssueLinks remoteLink;
    @UiField
    HTMLPanel message;
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
    @Inject
    @UiField(provided = true)
    TimeLabel timeElapsed;
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
    @UiField
    LIElement options;
    @UiField
    ImageElement icon;
    @Inject
    @UiField
    Lang lang;
    @Inject
    En_CaseStateLang stateLang;
    @Inject
    En_CaseImportanceLang importanceLang;

    private AbstractIssueCommentItemActivity activity;

    interface IssueCommentUiBinder extends UiBinder<Widget, IssueCommentItemView> {}
    private static IssueCommentUiBinder ourUiBinder = GWT.create( IssueCommentUiBinder.class );
}