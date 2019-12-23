package ru.protei.portal.ui.issue.client.view.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.makdown.MarkdownAreaWithPreview;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.issue.client.activity.create.AbstractIssueCreateActivity;
import ru.protei.portal.ui.issue.client.activity.create.AbstractIssueCreateView;

/**
 * Вид создания обращения
 */
public class IssueCreateView extends Composite implements AbstractIssueCreateView {
    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        description.setRenderer((text, consumer) -> activity.renderMarkupText(text, consumer));
        description.setDisplayPreviewHandler(isDisplay -> activity.onDisplayPreviewChanged(DESCRIPTION, isDisplay));
    }

    @Override
    public void setActivity(AbstractIssueCreateActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<String> description() {
        return description;
    }

    @Override
    public HasValue<Boolean> isPrivate() {
        return privacyButton;
    }

    @Override
    public HasValidable nameValidator() {
        return nameValidator;
    }

    @Override
    public HasAttachments attachmentsContainer() {
        return attachmentContainer;
    }

    @Override
    public void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler) {
        fileUploader.setUploadHandler(handler);
    }

    @Override
    public HasVisibility saveVisibility() {
        return saveButton;
    }

    @Override
    public HasVisibility privacyVisibility() {
        return privacyButton;
    }

    @Override
    public HasEnabled saveEnabled() {
        return saveButton;
    }

    @Override
    public HasWidgets getTagsContainer() {
        return tagsContainer;
    }

    @Override
    public HasWidgets getLinksContainer() {
        return linksContainer;
    }

    @Override
    public void setDescriptionPreviewAllowed(boolean isPreviewAllowed) {
        description.setDisplayPreview(isPreviewAllowed);
    }

    @Override
    public HasWidgets getIssueMetaViewContainer() {
        return issueMetaViewContainer;
    }

    @UiHandler("saveButton")
    public void onSaveClicked(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler("cancelButton")
    public void onCancelClicked(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }

    }

    @UiHandler("attachmentContainer")
    public void attachmentContainerRemove(RemoveEvent event) {
        activity.removeAttachment(event.getAttachment());
    }

    @UiHandler("privacyButton")
    public void onLocalClick(ClickEvent event) {
        if (activity != null) {
            activity.onPrivacyChanged();
        }
    }

    @UiHandler("addTagButton")
    public void onAddTagButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onAddTagClicked(addTagButton);
        }
    }

    @UiHandler("addLinkButton")
    public void onAddLinkButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onAddLinkClicked(addLinkButton);
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        privacyButton.ensureDebugId(DebugIds.ISSUE.PRIVACY_BUTTON);
        name.ensureDebugId(DebugIds.ISSUE.NAME_INPUT);
        description.setEnsureDebugId(DebugIds.ISSUE.DESCRIPTION_INPUT);
        fileUploader.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_UPLOAD_BUTTON);
        attachmentContainer.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_LIST_CONTAINER);
        saveButton.ensureDebugId(DebugIds.ISSUE.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.ISSUE.CANCEL_BUTTON);
        addTagButton.ensureDebugId(DebugIds.ISSUE.TAGS_BUTTON);
        addLinkButton.ensureDebugId(DebugIds.ISSUE.LINKS_BUTTON);

        nameLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.NAME);
        descriptionLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.INFO);
        attachmentsLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.ATTACHMENTS);
    }

    @UiField
    Lang lang;

    @UiField
    HTMLPanel root;

    @UiField
    TextBox name;

    @UiField
    MarkdownAreaWithPreview description;

    @UiField
    ToggleButton privacyButton;

    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;

    @Inject
    @UiField
    AttachmentUploader fileUploader;
    @Inject
    @UiField(provided = true)
    AttachmentList attachmentContainer;
    @UiField
    LabelElement descriptionLabel;
    @UiField
    LabelElement attachmentsLabel;
    @UiField
    HTMLPanel nameContainer;
    @UiField
    LabelElement nameLabel;
    @UiField
    HTMLPanel descriptionContainer;
    @UiField
    HTMLPanel linksContainer;
    @UiField
    HTMLPanel tagsContainer;
    @UiField
    HTMLPanel issueMetaViewContainer;
    @UiField
    Button addTagButton;
    @UiField
    Button addLinkButton;

    private HasValidable nameValidator = new HasValidable() {
        @Override
        public void setValid(boolean isValid) {
            if ( isValid ) {
                nameContainer.removeStyleName("has-error");
            } else {
                nameContainer.addStyleName("has-error");
            }
        }

        @Override
        public boolean isValid() {
            return HelperFunc.isNotEmpty(name.getValue());
        }
    };

    private AbstractIssueCreateActivity activity;

    interface IssueCreateViewUiBinder extends UiBinder<HTMLPanel, IssueCreateView> {
    }
    private static IssueCreateViewUiBinder ourUiBinder = GWT.create(IssueCreateViewUiBinder.class);
}