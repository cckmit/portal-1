package ru.protei.portal.ui.issue.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.ImportanceStyleProvider;
import ru.protei.portal.ui.common.client.lang.En_CaseImportanceLang;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.casemeta.CaseMetaView;
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
    public void setActivity( AbstractIssuePreviewActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setPrivateIssue( boolean privateIssue ) {
        if ( privateIssue ) {
            this.privateIssue.setClassName( "fa fa-lock text-danger m-r-10" );
            return;
        }

        this.privateIssue.setClassName( "fa fa-unlock-alt text-success m-r-10"  );
    }

    @Override
    public void setCreatedBy(String value) {
        this.createdBy.setInnerHTML( value );
    }

    @Override
    public void setState( long value ) {
        En_CaseState caseState = En_CaseState.getById( value );
        this.state.setInnerHTML( "<i class='fas fa-circle m-r-5 state-" + caseState.toString().toLowerCase() + "'></i>" +
                caseStateLang.getStateName( caseState ) );
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
    public void setLinks( Set<CaseLink> value ) {
        this.caseMetaView.setLinks( value );
    }

    @Override
    public void setTags(Set<CaseTag> value) {
        this.caseMetaView.setTags(value);
    }

    @Override
    public void setContact( String value ) {
        this.contact.setInnerText( value );
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
    public String getName() {
        return name.getInnerText();
    }

    @Override
    public void setInfo( String value ) {
        this.info.setInnerHTML( value );
    }

    @Override
    public void setSubscriptionEmails( String value ) {
        this.subscriptions.setInnerText( value );
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
        number.setText("CRM-" + caseNumber);
        fileUploader.autoBindingToCase(En_CaseType.CRM_SUPPORT, caseNumber);
    }

    @Override
    public void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler){
        fileUploader.setUploadHandler(handler);
    }

    @Override
    public HasVisibility backBtnVisibility() {
        return backButtonContainer;
    }

    @Override
    public HasVisibility timeElapsedContainerVisibility() {
        return timeElapsedContainer;
    }

    @Override
    public void setJiraVisible(boolean isVisible) {
        jiraMetaDataContainer.setVisible(isVisible);
    }

    @Override
    public void setJiraIssueType(String value) {
        if (StringUtils.isBlank(value)) {
            jiraIssueTypeContainer.setVisible(false);
        } else {
            jiraIssueType.setInnerText(value);
            jiraIssueTypeContainer.setVisible(true);
        }
    }

    @Override
    public void setJiraSeverity(String value) {
        if (StringUtils.isBlank(value)) {
            jiraSeverityContainer.setVisible(false);
        } else {
            jiraSeverity.setInnerText(value);
            jiraSeverityContainer.setVisible(true);
        }
    }

    @Override
    public void setJiraTimeOfReaction(String value) {
        if (StringUtils.isBlank(value)) {
            jiraTimeOfReactionContainer.setVisible(false);
        } else {
            jiraTimeOfReaction.setInnerText(value);
            jiraTimeOfReactionContainer.setVisible(true);
        }
    }

    @Override
    public void setJiraTimeOfDecision(String value) {
        if (StringUtils.isBlank(value)) {
            jiraTimeOfDecisionContainer.setVisible(false);
        } else {
            jiraTimeOfDecision.setInnerText(value);
            jiraTimeOfDecisionContainer.setVisible(true);
        }
    }

    @Override
    public HasTime timeElapsed() {
        return timeElapsed;
    }

    @Override
    public void setPlatform(String value) {
        this.platform.setText(value);
    }

    @Override
    public HasVisibility platformVisibility() {
        return platform;
    }

    @UiHandler( "number" )
    public void onFullScreenClicked ( ClickEvent event) {
        event.preventDefault();

        if ( activity != null ) {
            activity.onFullScreenPreviewClicked();
        }
    }

    @UiHandler( "backButton" )
    public void onGoToIssuesClicked ( ClickEvent event) {
        if ( activity != null ) {
            activity.onGoToIssuesClicked();
        }
    }

    @UiHandler("platform")
    public void onPlatformExtLinkClicked(ClickEvent event) {
        event.preventDefault();

        if (activity != null) {
            activity.onPlatformExtLinkClicked();
        }
    }

    @UiHandler("attachmentContainer")
    public void attachmentContainerRemove(RemoveEvent event) {
        activity.removeAttachment(event.getAttachment());
    }

    @UiHandler("copy")
    public void onCopyClick(ClickEvent event) {
        if ( activity != null ) {
            activity.onCopyClicked();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        privateIssue.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.PRIVACY_ICON);
        privateIssue.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.PRIVACY_ICON);
        number.ensureDebugId(DebugIds.ISSUE_PREVIEW.FULL_SCREEN_BUTTON);
        caseMetaView.setEnsureDebugIdContainer(DebugIds.ISSUE_PREVIEW.LINKS_CONTAINER);
        createdBy.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.DATE_CREATED_LABEL);
        criticality.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.IMPORTANCE_LABEL);
        product.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.PRODUCT_LABEL);
        state.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.STATE_LABEL);
        timeElapsed.ensureDebugId(DebugIds.ISSUE_PREVIEW.TIME_ELAPSED_LABEL);
        contact.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.CONTACT_LABEL);
        manager.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.MANAGER_LABEL);
        subscriptions.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.SUBSCRIPTION_LABEL);
        name.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.NAME_LABEL);
        platform.ensureDebugId(DebugIds.ISSUE_PREVIEW.PLATFORM_LABEL);
        info.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.INFO_LABEL);
        fileUploader.setEnsureDebugId(DebugIds.ISSUE_PREVIEW.ATTACHMENT_UPLOAD_BUTTON);
        attachmentContainer.setEnsureDebugId(DebugIds.ISSUE_PREVIEW.ATTACHMENT_LIST_CONTAINER);
        copy.ensureDebugId(DebugIds.ISSUE_PREVIEW.COPY_TO_CLIPBOARD_BUTTON);
    }

    @UiField
    HTMLPanel preview;
    @UiField
    Element privateIssue;
    @UiField
    Element createdBy;
    @UiField
    SpanElement product;
    @UiField
    DivElement state;
    @UiField
    Element iconCriticality;
    @UiField
    SpanElement criticality;
    @UiField
    SpanElement contact;
    @UiField
    SpanElement manager;
    @UiField
    HeadingElement name;
    @UiField
    Anchor platform;
    @UiField
    DivElement info;
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
    Element subscriptions;
    @UiField
    HTMLPanel jiraMetaDataContainer;
    @UiField
    HTMLPanel jiraIssueTypeContainer;
    @UiField
    HTMLPanel jiraSeverityContainer;
    @UiField
    HTMLPanel jiraTimeOfReactionContainer;
    @UiField
    HTMLPanel jiraTimeOfDecisionContainer;
    @UiField
    SpanElement jiraIssueType;
    @UiField
    SpanElement jiraSeverity;
    @UiField
    SpanElement jiraTimeOfReaction;
    @UiField
    SpanElement jiraTimeOfDecision;
    @UiField
    HTMLPanel timeElapsedContainer;
    @Inject
    @UiField(provided = true)
    TimeLabel timeElapsed;
    @Inject
    @UiField(provided = true)
    CaseMetaView caseMetaView;
    @UiField
    Button backButton;
    @UiField
    Anchor number;
    @UiField
    HTMLPanel backButtonContainer;
    @UiField
    Anchor copy;
    @Inject
    En_CaseImportanceLang caseImportanceLang;
    @Inject
    En_CaseStateLang caseStateLang;
    @UiField
    HTMLPanel numberCopyPanel;

    AbstractIssuePreviewActivity activity;

    interface IssuePreviewViewUiBinder extends UiBinder<HTMLPanel, IssuePreviewView> {}
    private static IssuePreviewViewUiBinder ourUiBinder = GWT.create( IssuePreviewViewUiBinder.class );
}