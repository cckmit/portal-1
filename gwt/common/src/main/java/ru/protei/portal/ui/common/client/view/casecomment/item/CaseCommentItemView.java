package ru.protei.portal.ui.common.client.view.casecomment.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseCommentPrivacyType;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.casecomment.item.AbstractCaseCommentItemListActivity;
import ru.protei.portal.ui.common.client.activity.casecomment.item.AbstractCaseCommentItemView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.throttler.Throttler;
import ru.protei.portal.ui.common.client.throttler.ThrottlerFactory;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.casecomment.item.EditTimeElapsedTypePopup;

import java.util.function.Consumer;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

/**
 * Один комментарий
 */
public class CaseCommentItemView
        extends Composite
        implements AbstractCaseCommentItemView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        setTestAttributes();
        image.addLoadHandler(loadEvent -> {
            if (image.getOffsetWidth() == image.getOffsetHeight()) {
                image.addStyleName("default-icon");
            }
        });
    }

    @Override
    public void setActivity( AbstractCaseCommentItemListActivity activity ) {
        this.activity = activity;
        attachList.setActivity(activity);
    }

    @Override
    public void setTimeElapsedTypeChangeHandler(Consumer<ValueChangeEvent<En_TimeElapsedType>> editTimeElapsedType) {
        timeElapsedTypePopup.addValueChangeHandler(editTimeElapsedType::accept);
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
    public void setMessage( String html ) {
        if ( html == null ) {
            this.message.getElement().setInnerText("");
            this.messageBlock.addClassName(  CrmConstants.Style.HIDE );
            this.hideOptions();
            return;
        }

        this.message.getElement().setInnerHTML(html);
        this.messageBlock.removeClassName(  CrmConstants.Style.HIDE );
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
    public HasVisibility timeElapsedVisibility() {
        return timeElapsed;
    }

    @Override
    public void showAttachments( boolean isShow ){
        if( isShow )
            attachBlock.removeClassName( CrmConstants.Style.HIDE );
        else
            attachBlock.addClassName(  CrmConstants.Style.HIDE );
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
    public void setImage(String url) {
        this.image.setUrl(url);
    }

    @Override
    public void setRemoteLinkNumber(String number) {
        if ( number == null ) {
            remoteLink.setVisible(false);
            return;
        }

        remoteLink.setVisible(true);
        remoteLink.setText(number);
    }

    @Override
    public void setRemoteLinkHref(String link) {
        if ( link == null ) {
            return;
        }

        remoteLink.setHref(link);
    }

    @Override
    public void setPrivacyType(En_CaseCommentPrivacyType value) {
        switch (value) {
            case PRIVATE:
                messageContainer.addClassName("private-message");
                privateType.setClassName("fa fa-lock text-danger m-l-10");
                break;
            case PRIVATE_CUSTOMERS:
                messageContainer.addClassName("private-customer-message");
                privateType.setClassName("fa fa-unlock text-warning m-l-10");
        }
    }

    @Override
    public void setTimeElapsedType(En_TimeElapsedType type) {
        timeElapsedTypePopup.setTimeElapsedType(type);
    }

    @Override
    public void displayUpdatedAnimation() {
        root.getElement().addClassName(CrmConstants.Style.UPDATED);
        removeUpdatedTimer.run();
    }

    @Override
    public void displayAddedAnimation() {
        root.getElement().addClassName(CrmConstants.Style.ADDED);
        removeAddedTimer.run();
    }

    @Override
    public HasVisibility timeElapsedTypePopupVisibility() {
        return timeElapsedTypePopup;
    }

    @Override
    public HasVisibility timeElapsedInfoContainerVisibility() {
        return timeElapsedInfoContainer;
    }

    @Override
    public void setTimeElapsedInfo(String timeElapsedInfo) {
        this.timeElapsedInfo.getElement().setInnerText(timeElapsedInfo);
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

    @UiHandler("timeElapsed")
    public void onTimeElapsedClicked(ClickEvent event) {
        activity.onTimeElapsedTypeClicked(this);
    }

    private void setTestAttributes() {
        privateType.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CASE_COMMENT.COMMENT_ITEM.PRIVACY_ICON);
        reply.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CASE_COMMENT.COMMENT_ITEM.REPLY_BUTTON);
        edit.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CASE_COMMENT.COMMENT_ITEM.EDIT_BUTTON);
        remove.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CASE_COMMENT.COMMENT_ITEM.REMOVE_BUTTON);
        timeElapsed.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CASE_COMMENT.COMMENT_ITEM.TIME_ELAPSED);
        date.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CASE_COMMENT.COMMENT_ITEM.CREATE_DATE);
        owner.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CASE_COMMENT.COMMENT_ITEM.OWNER);
        timeElapsedTypePopup.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CASE_COMMENT.COMMENT_ITEM.EDIT_TIME_ELAPSED_TYPE_POPUP);
    }


    @UiField
    Anchor remoteLink;
    @UiField
    HTMLPanel message;
    @UiField
    HTMLPanel timeElapsedInfoContainer;
    @UiField
    HTMLPanel timeElapsedInfo;
    @UiField
    Element privateType;
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
    Anchor timeElapsed;
    @UiField
    DivElement attachBlock;
    @UiField
    DivElement messageBlock;
    @UiField
    LIElement date;
    @UiField
    LIElement owner;
    @UiField
    LIElement options;
    @Inject
    @UiField(provided = true)
    EditTimeElapsedTypePopup timeElapsedTypePopup;
    @UiField
    DivElement messageContainer;
    @UiField
    Image image;

    @Inject
    @UiField
    Lang lang;
    Throttler removeUpdatedTimer = ThrottlerFactory.makeDelayedAntiRapidThrottler( 1 * SECOND, ()-> root.getElement().removeClassName( CrmConstants.Style.UPDATED ) );
    Throttler removeAddedTimer = ThrottlerFactory.makeDelayedAntiRapidThrottler( 1 * SECOND,  () -> root.getElement().removeClassName( CrmConstants.Style.ADDED ) );

    private AbstractCaseCommentItemListActivity activity;

    private static int SECOND = (int) CrmConstants.Time.SEC;

    interface CaseCommentUiBinder extends UiBinder<Widget, CaseCommentItemView> {}
    private static CaseCommentUiBinder ourUiBinder = GWT.create( CaseCommentUiBinder.class );
}
