package ru.protei.portal.ui.issue.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditActivity;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditView;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

/**
 * Вид создания и редактирования обращения
 */
public class IssueEditView extends Composite implements AbstractIssueEditView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();

        copyNumber.getElement().setAttribute( "title", lang.issueCopyNumber() );
    }

    @Override
    public void setActivity( AbstractIssueEditActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getMetaContainer() {
        return metaEditContainer;
    }

    @Override
    public HasWidgets getNameInfoContainer() {
        return issueInfoContainer;
    }

    @Override
    public HasWidgets getLinksContainer() {
        return linksContainer;
    }

    @Override
    public HasWidgets getCommentsContainer() {
        return commentsContainer;
    }

    @Override
    public HasAttachments attachmentsContainer() {
        return attachmentContainer;
    }

    @Override
    public void setFileUploadHandler( AttachmentUploader.FileUploadHandler handler ) {
        fileUploader.setUploadHandler( handler );
    }

    @Override
    public void setCaseNumber( Long caseNumber ) {
        number.setText( lang.crmPrefix() + caseNumber );
        fileUploader.autoBindingToCase( En_CaseType.CRM_SUPPORT, caseNumber );
    }

    @Override
    public void setPrivateIssue( boolean isPrivate ) {
        if (isPrivate) {
            privacyIcon.setClassName( "fas fa-lock text-danger m-l-10" );
            privacyIcon.setAttribute( DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.PRIVACY_ICON_PRIVATE );
        } else {
            privacyIcon.setClassName( "fas fa-unlock text-success m-l-10" );
            privacyIcon.setAttribute( DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.PRIVACY_ICON_PUBLIC );
        }
    }

    @Override
    public HasWidgets getTagsContainer() {
        return tagsContainer;
    }

//    public void setDescriptionRO( String value) {
//        this.nameRO.setInnerHTML(value);
//        this.nameRO.setInnerHTML("");
//        this.nameRO.appendChild(jiraLink);
//        this.nameRO.appendChild(nameWithoutLink);
//    }

    @Override
    public void setCreatedBy(String value) {
        this.createdBy.setInnerHTML( value );
    }

    @Override
    public HasVisibility editNameAndDescriptionButtonVisibility() {
        return editNameAndDescriptionButton;
    }

    @UiHandler("attachmentContainer")
    public void attachmentContainerRemove(RemoveEvent event) {
        activity.removeAttachment(event.getAttachment());
    }

    @UiHandler("copyNumber")
    public void onCopyNumberClick(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onCopyNumberClicked();
        }
    }

    @UiHandler("editNameAndDescriptionButton")
    public void onEditNameAndDescriptionButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onEditNameAndDescriptionClicked(this);
        }
    }

    @UiHandler( "number" )
    public void onFullScreenClicked ( ClickEvent event) {
        event.preventDefault();

        if ( activity != null ) {
            activity.onFullScreenPreviewClicked();
        }
    }

    @UiHandler("backButton")
    public void onBackButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onBackClicked();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        privacyIcon.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.PRIVACY_ICON);
        number.ensureDebugId(DebugIds.ISSUE_PREVIEW.FULL_SCREEN_BUTTON);
        nameRO.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.NAME_FIELD);
        fileUploader.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_UPLOAD_BUTTON);
        attachmentContainer.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_LIST_CONTAINER);
        copyNumber.ensureDebugId(DebugIds.ISSUE.COPY_NUMBER_BUTTON);
        attachmentsLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.ATTACHMENTS);
    }

    @UiField
    Lang lang;
    @UiField
    HTMLPanel root;
    @UiField
    Anchor copyNumber;
    @UiField
    HTMLPanel commentsContainer;
    @UiField
    DivElement comments;
    @Inject
    @UiField
    AttachmentUploader fileUploader;
    @Inject
    @UiField(provided = true)
    AttachmentList attachmentContainer;
    @UiField
    LabelElement attachmentsLabel;
    @UiField
    Anchor number;
    @UiField
    Element createdBy;
    @UiField
    HTMLPanel numberPanel;
    @UiField
    Element privacyIcon;
    @UiField
    HTMLPanel linksContainer;
    @UiField
    HTMLPanel tagsContainer;
    @UiField
    HTMLPanel metaEditContainer;
    @UiField
    Button editNameAndDescriptionButton;
    @UiField
    HTMLPanel cardBody;
    @UiField
    HTMLPanel issueInfoContainer;
    @UiField
    DivElement attachmentsPanel;
    @UiField
    DivElement commentsPanel;
    @UiField
    Button backButton;

    @UiField
    HTMLPanel namePanel;
    @UiField
    HeadingElement nameROPanel;
    @UiField
    LabelElement nameRO;
    @UiField
    HTMLPanel descriptionPanel;


    private AbstractIssueEditActivity activity;


    interface IssueEditViewUiBinder extends UiBinder<HTMLPanel, IssueEditView> {}
    private static IssueEditViewUiBinder ourUiBinder = GWT.create(IssueEditViewUiBinder.class);
}