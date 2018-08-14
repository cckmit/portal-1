package ru.protei.portal.ui.issue.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.ImportanceStyleProvider;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.common.ImportanceStyleProvider;
import ru.protei.portal.ui.common.client.lang.En_CaseImportanceLang;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.issuelinks.list.IssueLinks;
import ru.protei.portal.ui.common.client.widget.timefield.HasTime;
import ru.protei.portal.ui.common.client.widget.timefield.TimeLabel;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.issue.client.activity.preview.AbstractIssuePreviewActivity;
import ru.protei.portal.ui.issue.client.activity.preview.AbstractIssuePreviewView;

import java.util.Set;

/**
 * Вид превью обращения
 */
public class IssuePreviewView extends Composite implements AbstractIssuePreviewView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        watchForScroll(false);
    }

    @Override
    public void watchForScroll(boolean isWatch) {
        if(isWatch)
            positioner.watch(this, FixedPositioner.NAVBAR_TOP_OFFSET);
        else
            positioner.ignore(this);
    }

    @Override
    public void setActivity( AbstractIssuePreviewActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setPrivateIssue( boolean privateIssue ) {
        if ( privateIssue ) {
            this.privateIssue.setClassName( "fa fa-fw fa-lg fa-lock text-danger pull-left" );
            return;
        }

        this.privateIssue.setClassName( "fa fa-fw fa-lg fa-unlock-alt text-success pull-left"  );
    }

    @Override
    public void setHeader( String value ) {
        this.header.setInnerText( value );
    }

    @Override
    public void setCreationDate( String value ) {
        this.creationDate.setInnerText( value );
    }

    @Override
    public void setState( long value ) {
        En_CaseState caseState = En_CaseState.getById( value );
        this.state.setClassName( "small label label-" + caseState.toString().toLowerCase());
        this.state.setInnerText( caseStateLang.getStateName( caseState ) );
    }

    @Override
    public void setCriticality( int value ) {
        En_ImportanceLevel importanceLevel = En_ImportanceLevel.find( value );
        this.iconCriticality.setClassName(ImportanceStyleProvider.getImportanceIcon(En_ImportanceLevel.getById(value)));
        this.criticality.setInnerText( caseImportanceLang.getImportanceName( importanceLevel ) );
    }

    @Override
    public void setProduct( String value ) {
        this.product.setInnerText( value );
    }

    @Override
    public void setCompany( String value ) {
        this.company.setInnerText( value );
    }

    @Override
    public void setLinks( Set<CaseLink> value ) {
        this.links.setValue( value );
    }

    @Override
    public void setContact( String value ) {
        this.contact.setInnerHTML( value );
    }

    @Override
    public void setOurCompany( String value ) {
        this.ourCompany.setInnerText( value );
    }

    @Override
    public void setManager( String value ) {
        this.manager.setInnerText( value );
    }

    @Override
    public void setName( String value ) {
        this.name.setInnerText( value );
    }

    @Override
    public void setInfo( String value ) {
        this.info.setInnerText( value );
    }

    @Override
    public void setSubscriptionEmails( String value ) {
        this.subscriptions.setInnerText( value );
    }

    @Override
    public void showFullScreen( boolean value ) {
        this.fullScreen.setVisible( !value );
        if ( value ) {
            this.preview.addStyleName( "col-xs-12 col-lg-6" );
        } else {
            this.preview.setStyleName( "preview" );
        }
    }

    @Override
    public HasWidgets getCommentsContainer() {
        return commentsContainer;
    }

    @Override
    public HasAttachments attachmentsContainer(){
        return attachmentContainer;
    }

    @Override
    public void setCaseNumber(Long caseNumber) {
        fileUploader.autoBindingToCase(caseNumber);
    }

    @Override
    public void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler){
        fileUploader.setUploadHandler(handler);
    }

    @Override
    public HasVisibility timeElapsedContainerVisibility() {
        return timeElapsedContainer;
    }

    @Override
    public HasTime timeElapsed() {
        return timeElapsed;
    }


    @UiHandler( "fullScreen" )
    public void onFullScreenClicked ( ClickEvent event) {
        event.preventDefault();

        if ( activity != null ) {
            activity.onFullScreenPreviewClicked();
        }
    }

    @UiHandler("attachmentContainer")
    public void attachmentContainerRemove(RemoveEvent event) {
        activity.removeAttachment(event.getAttachment());
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        privateIssue.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.PRIVACY_ICON);
        privateIssue.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.PRIVACY_ICON);
        fullScreen.ensureDebugId(DebugIds.ISSUE_PREVIEW.FULL_SCREEN_BUTTON);
        header.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.TITLE_LABEL);
        links.setEnsureDebugIdContainer(DebugIds.ISSUE_PREVIEW.LINKS_CONTAINER);
        creationDate.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.DATE_CREATED_LABEL);
        criticality.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.IMPORTANCE_LABEL);
        product.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.PRODUCT_LABEL);
        state.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.STATE_LABEL);
        timeElapsed.ensureDebugId(DebugIds.ISSUE_PREVIEW.TIME_ELAPSED_LABEL);
        company.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.COMPANY_LABEL);
        contact.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.CONTACT_LABEL);
        ourCompany.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.OUR_COMPANY_LABEL);
        manager.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.MANAGER_LABEL);
        subscriptions.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.SUBSCRIPTION_LABEL);
        name.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.NAME_LABEL);
        info.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.INFO_LABEL);
        fileUploader.setEnsureDebugId(DebugIds.ISSUE_PREVIEW.ATTACHMENT_UPLOAD_BUTTON);
        attachmentContainer.setEnsureDebugId(DebugIds.ISSUE_PREVIEW.ATTACHMENT_LIST_CONTAINER);
    }

    @UiField
    HTMLPanel preview;
    @UiField
    Anchor fullScreen;
    @UiField
    Element privateIssue;
    @UiField
    Element header;
    @UiField
    SpanElement creationDate;
    @UiField
    SpanElement product;
    @UiField
    DivElement state;
    @UiField
    Element iconCriticality;
    @UiField
    SpanElement criticality;
    @UiField
    LabelElement company;
    @UiField
    SpanElement contact;
    @UiField
    LabelElement ourCompany;
    @UiField
    SpanElement manager;
    @UiField
    SpanElement name;
    @UiField
    SpanElement info;
    @Inject
    @UiField
    Lang lang;
    @UiField
    HTMLPanel commentsContainer;
    @Inject
    @UiField
    AttachmentUploader fileUploader;
    @Inject
    @UiField(provided = true)
    AttachmentList attachmentContainer;
    @UiField
    DivElement subscriptions;
    @UiField
    HTMLPanel timeElapsedContainer;
    @Inject
    @UiField(provided = true)
    TimeLabel timeElapsed;
    @Inject
    @UiField(provided = true)
    IssueLinks links;
    @Inject
    En_CaseImportanceLang caseImportanceLang;
    @Inject
    En_CaseStateLang caseStateLang;
    @Inject
    FixedPositioner positioner;

    AbstractIssuePreviewActivity activity;

    interface IssuePreviewViewUiBinder extends UiBinder<HTMLPanel, IssuePreviewView> {}
    private static IssuePreviewViewUiBinder ourUiBinder = GWT.create( IssuePreviewViewUiBinder.class );
}