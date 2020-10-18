package ru.protei.portal.ui.issue.client.view.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.accordion.AccordionWidget;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.attachment.list.fullview.FullViewAttachmentList;
import ru.protei.portal.ui.common.client.widget.makdown.MarkdownAreaWithPreview;
import ru.protei.portal.ui.common.client.widget.uploader.impl.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.uploader.impl.buttonpanel.ButtonPanelAttachmentUploader;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.issue.client.activity.create.AbstractIssueCreateActivity;
import ru.protei.portal.ui.issue.client.activity.create.AbstractIssueCreateView;

import static ru.protei.portal.core.model.util.CrmConstants.NAME_MAX_SIZE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Icons.FAVORITE_ACTIVE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Icons.FAVORITE_NOT_ACTIVE;

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
        description.setFileUploader(fileUploader);
        description.setDropZonePanel(dropPanel);
        name.setMaxLength(NAME_MAX_SIZE);

        accordionWidget.setLocalStorageKey(UiConstants.ATTACHMENTS_PANEL_VISIBILITY);
        accordionWidget.setMaxHeight(UiConstants.Accordion.ATTACHMENTS_MAX_HEIGHT);
    }

    @Override
    public void setActivity(AbstractIssueCreateActivity activity) {
        this.activity = activity;
        attachmentListContainer.setActivity(activity);
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
    public HasAttachments attachmentsListContainer() {
        return attachmentListContainer;
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

    @Override
    public boolean isFavoriteButtonActive() {
        return favoriteButtonIcon.hasClassName(FAVORITE_ACTIVE);
    }

    @Override
    public void setFavoriteButtonActive(boolean isActive) {
        if (isActive) {
            favoriteButtonIcon.replaceClassName(FAVORITE_NOT_ACTIVE, FAVORITE_ACTIVE);
            favoritesButton.setTitle(lang.issueRemoveFromFavorites());
        } else {
            favoriteButtonIcon.replaceClassName(FAVORITE_ACTIVE, FAVORITE_NOT_ACTIVE);
            favoritesButton.setTitle(lang.issueAddToFavorites());
        }
    }

    @Override
    public void setCountOfAttachments(int countOfAttachments) {
        //accordionWidget.setHeader(lang.attachmentsHeader(String.valueOf(countOfAttachments)));
    }

    @Override
    public HasVisibility attachmentsVisibility() {
        return accordionWidget;
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

    @UiHandler("attachmentListContainer")
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

    @UiHandler("favoritesButton")
    public void onFavoriteStateChanged(ClickEvent event) {
        if (activity != null) {
            activity.onFavoriteStateChanged();
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
        attachmentListContainer.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_LIST_CONTAINER);
        saveButton.ensureDebugId(DebugIds.ISSUE.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.ISSUE.CANCEL_BUTTON);
        addTagButton.ensureDebugId(DebugIds.ISSUE.TAGS_BUTTON);
        addLinkButton.ensureDebugId(DebugIds.ISSUE.LINKS_BUTTON);
        favoritesButton.ensureDebugId(DebugIds.ISSUE.FAVORITES_BUTTON);

        nameLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.NAME);
        descriptionLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.INFO);
        //accordionWidget.setHeaderLabelDebugId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.ATTACHMENTS);
        accordionWidget.setCollapseButtonDebugId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.ATTACHMENT_COLLAPSE_BUTTON);
    }

    @UiField
    Lang lang;

    @UiField
    HTMLPanel root;

    @UiField
    ValidableTextBox name;
    @UiField
    HTMLPanel dropPanel;
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
    ButtonPanelAttachmentUploader fileUploader;
    @Inject
    @UiField(provided = true)
    FullViewAttachmentList attachmentListContainer;
    @UiField
    LabelElement descriptionLabel;
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
    Button favoritesButton;
    @UiField
    Element favoriteButtonIcon;
    @UiField
    HTMLPanel issueMetaViewContainer;
    @UiField
    Button addTagButton;
    @UiField
    Button addLinkButton;
    @Inject
    @UiField(provided = true)
    AccordionWidget accordionWidget;

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
