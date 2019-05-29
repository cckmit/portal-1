package ru.protei.portal.ui.common.client.view.casecomment.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
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
import ru.protei.portal.ui.common.client.activity.casecomment.item.AbstractCaseCommentItemActivity;
import ru.protei.portal.ui.common.client.activity.casecomment.item.AbstractCaseCommentItemView;
import ru.protei.portal.ui.common.client.lang.En_CaseImportanceLang;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.casemeta.CaseMetaView;

import java.util.HashSet;
import java.util.Set;

/**
 * Один комментарий
 */
public class CaseCommentItemView
        extends Composite
        implements AbstractCaseCommentItemView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity( AbstractCaseCommentItemActivity activity ) {
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
    public void setMessage( String html ) {
        if ( html == null ) {
            this.message.getElement().setInnerText("");
            this.messageBlock.addClassName( "hide" );
            this.hideOptions();
            return;
        }

        this.message.getElement().setInnerHTML(html);
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
    public void setTimeElapsed( String timeTypeString ) {
        timeElapsed.setInnerHTML( timeTypeString == null ? "" : timeTypeString );
    }

    @Override
    public void clearElapsedTime() {
        timeElapsed.setInnerHTML("");
    }

    @Override
    public void setRemoteLink(CaseLink remoteLink) {
        Set<CaseLink> set = new HashSet<>();
        if (remoteLink != null)
            set.add(remoteLink);
        this.remoteLink.setLinks(set);
        this.remoteLink.setVisible(remoteLink != null);
    }

    @Override
    public void setPrivateComment(Boolean value) {
        privateComment.setClassName(value ? "fas fa-lock fa-lg text-danger"
                                          : "fas fa-unlock-alt fa-lg text-success");
    }

    private HasVisibility privacyVisibility = new HasVisibility() {
        @Override
        public boolean isVisible() {
            return privateComment.getClassName().contains("hide") ;
        }

        @Override
        public void setVisible( boolean b ) {
            if (b) {
                privateComment.removeClassName("hide");
            } else {
                privateComment.setClassName("hide");
            }
        }
    };

    @Override
    public HasVisibility getPrivacyVisibility() {
        return privacyVisibility;
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
    CaseMetaView remoteLink;
    @UiField
    HTMLPanel message;
    @UiField
    Element privateComment;
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
    LIElement timeElapsed;
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

    private AbstractCaseCommentItemActivity activity;

    interface CaseCommentUiBinder extends UiBinder<Widget, CaseCommentItemView> {}
    private static CaseCommentUiBinder ourUiBinder = GWT.create( CaseCommentUiBinder.class );
}