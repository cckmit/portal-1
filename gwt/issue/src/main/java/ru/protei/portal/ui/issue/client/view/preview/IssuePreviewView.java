package ru.protei.portal.ui.issue.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.*;
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

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

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
    public void setPrivateIssue( boolean isPrivate ) {
        if ( isPrivate ) {
            privateIssue.setClassName( "fa fa-lock text-danger m-r-10" );
            privateIssue.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.PRIVACY_ICON_PRIVATE);
        } else {
            privateIssue.setClassName( "fa fa-unlock-alt text-success m-r-10"  );
            privateIssue.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.PRIVACY_ICON_PUBLIC);
        }
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
    public void setImportance(int value ) {
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
    public HasVisibility jiraContainerVisibility() {
        return jiraMetaDataContainer;
    }

    @Override
    public void setJiraIssueType(String value) {
        jiraIssueType.setInnerText(StringUtils.isEmpty(value) ? "" : value);
    }

    @Override
    public void setJiraSeverity(String value) {
        jiraSeverity.setInnerText(StringUtils.isEmpty(value) ? "" : value);
    }

    @Override
    public void setJiraTimeOfReaction(String value) {
        jiraTimeOfReaction.setInnerText(StringUtils.isEmpty(value) ? "" : value);
    }

    @Override
    public void setJiraTimeOfDecision(String value) {
        jiraTimeOfDecision.setInnerText(StringUtils.isEmpty(value) ? "" : value);
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
    public void setPlatformVisibility(boolean visible) {
        if (visible) {
            platformContainer.removeClassName("hide");
            productContainer.replaceClassName("col-md-6", "col-md-3");
        } else {
            platformContainer.addClassName("hide");
            productContainer.replaceClassName("col-md-3", "col-md-6");
        }
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
        event.preventDefault();
        if ( activity != null ) {
            activity.onCopyClicked();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        privateIssue.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.PRIVACY_ICON);
        number.ensureDebugId(DebugIds.ISSUE_PREVIEW.FULL_SCREEN_BUTTON);
        caseMetaView.setEnsureDebugIdLinkContainer(DebugIds.ISSUE_PREVIEW.LINKS_CONTAINER);
        createdBy.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.DATE_CREATED);
        criticalityLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.LABEL.IMPORTANCE);
        criticality.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.IMPORTANCE);
        productLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.LABEL.PRODUCT);
        product.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.PRODUCT);
        stateLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.LABEL.STATE);
        state.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.STATE);
        timeElapsedLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.LABEL.TIME_ELAPSED);
        timeElapsed.ensureDebugId(DebugIds.ISSUE_PREVIEW.TIME_ELAPSED);
        contactLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.LABEL.CONTACT);
        contact.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.CONTACT);
        managerLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.LABEL.MANAGER);
        manager.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.MANAGER);
        subscriptionsLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.LABEL.SUBSCRIPTION);
        subscriptions.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.SUBSCRIPTION);
        name.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.NAME);
        platformLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.LABEL.PLATFORM);
        platform.ensureDebugId(DebugIds.ISSUE_PREVIEW.PLATFORM);
        info.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.INFO);
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
    @UiField
    LabelElement stateLabel;
    @UiField
    LabelElement productLabel;
    @UiField
    LabelElement contactLabel;
    @UiField
    LabelElement managerLabel;
    @UiField
    HeadingElement subscriptionsLabel;
    @UiField
    LabelElement platformLabel;
    @UiField
    LabelElement criticalityLabel;
    @UiField
    SpanElement timeElapsedLabel;
    @UiField
    DivElement productContainer;
    @UiField
    DivElement platformContainer;

    AbstractIssuePreviewActivity activity;

    interface IssuePreviewViewUiBinder extends UiBinder<HTMLPanel, IssuePreviewView> {}
    private static IssuePreviewViewUiBinder ourUiBinder = GWT.create( IssuePreviewViewUiBinder.class );
}