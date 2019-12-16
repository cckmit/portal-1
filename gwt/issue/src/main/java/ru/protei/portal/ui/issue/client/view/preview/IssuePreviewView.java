package ru.protei.portal.ui.issue.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.ImportanceStyleProvider;
import ru.protei.portal.ui.common.client.lang.En_CaseImportanceLang;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.timefield.HasTime;
import ru.protei.portal.ui.common.client.widget.timefield.TimeLabel;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.issue.client.activity.preview.AbstractIssuePreviewActivity;
import ru.protei.portal.ui.issue.client.activity.preview.AbstractIssuePreviewView;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

/**
 * Вид превью обращения
 */
public class IssuePreviewView extends Composite implements AbstractIssuePreviewView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        copyNumber.getElement().setAttribute("title", lang.issueCopyNumber());
        ensureDebugIds();
    }

    @Override
    public void setActivity( AbstractIssuePreviewActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setPrivateIssue( boolean isPrivate ) {
        if ( isPrivate ) {
            privateIssue.setClassName( "fa fa-lock text-danger m-l-10" );
            privateIssue.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.PRIVACY_ICON_PRIVATE);
        } else {
            privateIssue.setClassName( "fa fa-unlock-alt text-success m-l-10"  );
            privateIssue.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.PRIVACY_ICON_PUBLIC);
        }
    }

    @Override
    public void setCreatedBy(String value) {
        this.createdBy.setInnerHTML( value );
    }

    @Override
    public HasVisibility editNameAndDescriptionButtonVisibility() {
        return editNameAndDescriptionButtonContainer;
    }

    @Override
    public HasWidgets getNameInfoContainer() {
        return issueInfoContainer;
    }

    @UiHandler("editNameAndDescriptionButton")
    public void onEditNameAndDescriptionButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onEditNameAndDescriptionClicked(this);
        }
    }

    @Override
    public HasWidgets getCommentsContainer() {
        return commentsContainer;
    }

    @Override
    public HasWidgets getLinksContainer() {
        return linksContainer;
    }

    @Override
    public HasAttachments attachmentsContainer(){
        return attachmentContainer;
    }

    @Override
    public void setCaseNumber(Long caseNumber) {
        number.setText(lang.crmPrefix() + caseNumber);
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
    public HasWidgets getTagsContainer() {
        return tagsContainer;
    }

    @Override
    public HasWidgets getMetaContainer() {
        return metaContainer;
    }

    @Override
    public void setFullScreen( boolean isFullScreen) {
        previewWrapperContainer.setStyleName("card card-transparent no-margin preview-wrapper card-with-fixable-footer", isFullScreen);
        if (isFullScreen) {
            metaContainer.addStyleName("p-r-15 p-l-15");
        } else {
            metaContainer.removeStyleName("p-r-15 p-l-15");
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

    @UiHandler("attachmentContainer")
    public void attachmentContainerRemove(RemoveEvent event) {
        activity.removeAttachment(event.getAttachment());
    }

    @UiHandler("copyNumber")
    public void onCopyClick(ClickEvent event) {
        event.preventDefault();
        if ( activity != null ) {
            activity.onCopyNumberClicked();
        }
    }


    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        privateIssue.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.PRIVACY_ICON);
        number.ensureDebugId(DebugIds.ISSUE_PREVIEW.FULL_SCREEN_BUTTON);
        createdBy.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.DATE_CREATED);
        info.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.INFO);
        fileUploader.setEnsureDebugId(DebugIds.ISSUE_PREVIEW.ATTACHMENT_UPLOAD_BUTTON);
        attachmentContainer.setEnsureDebugId(DebugIds.ISSUE_PREVIEW.ATTACHMENT_LIST_CONTAINER);
        copyNumber.ensureDebugId(DebugIds.ISSUE_PREVIEW.COPY_NUMBER_BUTTON);
    }

    @UiField
    HTMLPanel cardBody;
    @UiField
    Element privateIssue;
    @UiField
    Element createdBy;
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
    Button backButton;
    @UiField
    Anchor number;
    @UiField
    HTMLPanel backButtonContainer;
    @UiField
    Anchor copyNumber;
    @UiField
    HTMLPanel previewWrapperContainer;
    @UiField
    HTMLPanel linksContainer;
    @UiField
    HTMLPanel tagsContainer;
    @UiField
    HTMLPanel metaContainer;
    @UiField
    Button editNameAndDescriptionButton;
    @UiField
    HTMLPanel issueInfoContainer;
    @UiField
    HTMLPanel editNameAndDescriptionButtonContainer;

    AbstractIssuePreviewActivity activity;

    interface IssuePreviewViewUiBinder extends UiBinder<HTMLPanel, IssuePreviewView> {}
    private static IssuePreviewViewUiBinder ourUiBinder = GWT.create( IssuePreviewViewUiBinder.class );
}