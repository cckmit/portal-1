package ru.protei.portal.ui.issue.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.issue.client.activity.edit.IssueEditActivity;

public class IssueInfoWidget extends Composite{
   
    @PostConstruct
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        copyNumberAndName.getElement().setAttribute( "title", lang.issueCopyNumberAndName() );
        ensureDebugIds();
    }

    public void setActivity( IssueEditActivity activity ) {
        this.activity = activity;
    }


    public HasWidgets getLinksContainer() {
        return linksContainer;
    }


    public HasWidgets getCommentsContainer() {
        return commentsContainer;
    }


    public HasAttachments attachmentsContainer() {
        return attachmentContainer;
    }
    

    public void setFileUploadHandler( AttachmentUploader.FileUploadHandler handler ) {
        fileUploader.setUploadHandler( handler );
    }


    public void setCaseNumber( Long caseNumber ) {
        fileUploader.autoBindingToCase( En_CaseType.CRM_SUPPORT, caseNumber );
    }

    public void setName( String issueName ) {
        nameRO.setInnerHTML( issueName );
    }

    public void setDescription( String issueDescription ) {
        descriptionRO.setInnerHTML( issueDescription );
    }

    @UiHandler("attachmentContainer")
    public void attachmentContainerRemove( RemoveEvent event) {
        activity.removeAttachment(event.getAttachment());
    }

    @UiHandler("copyNumberAndName")
    public void onCopyNumberAndNameClick( ClickEvent event ) {
        event.preventDefault();
        activity.onCopyNumberAndName();

    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        nameRO.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.NAME_FIELD);
        descriptionRO.setId( DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.DESCRIPTION_FIELD );
        fileUploader.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_UPLOAD_BUTTON);
        attachmentContainer.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_LIST_CONTAINER);
        attachmentsLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.ATTACHMENTS);
        copyNumberAndName.ensureDebugId( DebugIds.ISSUE.COPY_NUMBER_AND_NAME_BUTTON );
    }

    @UiField
    Lang lang;

    @UiField
    HTMLPanel linksContainer;
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
    DivElement attachmentsPanel;
    @UiField
    DivElement commentsPanel;
    @UiField
    HeadingElement nameROPanel;
    @UiField
    Anchor copyNumberAndName;
    @UiField
    LabelElement nameRO;
    @UiField
    DivElement descriptionRO;

    private IssueEditActivity activity;

    interface IssueInfoWidgetUiBinder extends UiBinder<HTMLPanel, IssueInfoWidget> {
    }

    private static IssueInfoWidgetUiBinder ourUiBinder = GWT.create( IssueInfoWidgetUiBinder.class );
}