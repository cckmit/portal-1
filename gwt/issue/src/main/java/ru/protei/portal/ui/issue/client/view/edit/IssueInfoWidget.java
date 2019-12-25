package ru.protei.portal.ui.issue.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditActivity;

public class IssueInfoWidget extends Composite{
   
    @PostConstruct
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
    }

    public void setActivity( AbstractIssueEditActivity activity ) {
        this.activity = activity;
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

    public void setDescription( String issueDescription ) {
        descriptionRO.setInnerHTML( issueDescription );
    }

    public HasVisibility attachmentUploaderVisibility() {
        return attachmentUploaderContainer;
    }

    @UiHandler("attachmentContainer")
    public void attachmentContainerRemove( RemoveEvent event) {
        activity.removeAttachment(event.getAttachment());
    }


    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        descriptionRO.setId( DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.DESCRIPTION_FIELD );
        fileUploader.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_UPLOAD_BUTTON);
        attachmentContainer.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_LIST_CONTAINER);
        attachmentsLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.ATTACHMENTS);
    }

    @UiField
    Lang lang;

    @UiField
    HTMLPanel commentsContainer;
    @UiField
    DivElement comments;
    @Inject
    @UiField
    AttachmentUploader fileUploader;
    @UiField
    HTMLPanel attachmentUploaderContainer;
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
    DivElement descriptionRO;

    private AbstractIssueEditActivity activity;

    interface IssueInfoWidgetUiBinder extends UiBinder<HTMLPanel, IssueInfoWidget> {
    }

    private static IssueInfoWidgetUiBinder ourUiBinder = GWT.create( IssueInfoWidgetUiBinder.class );
}