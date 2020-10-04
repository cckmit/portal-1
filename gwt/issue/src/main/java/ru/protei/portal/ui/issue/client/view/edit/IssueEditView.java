package ru.protei.portal.ui.issue.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.uploader.AbstractAttachmentUploader;
import ru.protei.portal.ui.common.client.widget.uploader.impl.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.uploader.impl.buttonpanel.ButtonPanelAttachmentUploader;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditActivity;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditView;

import static ru.protei.portal.core.model.helper.StringUtils.emptyIfNull;
import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Icons.FAVORITE_ACTIVE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Icons.FAVORITE_NOT_ACTIVE;

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
        nameWidget.setActivity( activity );
    }

    @Override
    public void setPreviewStyles(boolean isPreview) {
        root.removeStyleName("card-default");
        root.removeStyleName("card-transparent");
        root.removeStyleName("card-fixed");
        if (isPreview) {
            root.addStyleName("card-default");
            root.addStyleName("card-fixed");
        } else {
            root.addStyleName("card-transparent");
        }
    }

    @Override
    public void setCaseNumber( Long caseNumber ) {
        number.setInnerText(lang.crmPrefix() + caseNumber);
        fileUploader.autoBindingToCase( En_CaseType.CRM_SUPPORT, caseNumber );
    }

    @Override
    public void setName( String issueName ) {
        nameWidget.setName( issueName );
    }

    @Override
    public void setIntegration(String name) {
        integrationLabelName.setInnerText(emptyIfNull(name));
        integrationLabel.setVisible(!isBlank(name));
    }

    @Override
    public HasWidgets getMetaContainer() {
        return metaEditContainer;
    }

    @Override
    public HasWidgets getLinksContainer() {
        return linksContainer;
    }

    @Override
    public HasVisibility nameVisibility() {
        return nameWidget;
    }

    @Override
    public HasVisibility backButtonVisibility() {
        return backButton;
    }

    @Override
    public HasVisibility showEditViewButtonVisibility() {
        return showEditViewButton;
    }

    @Override
    public HasVisibility nameAndDescriptionEditButtonVisibility() {
        return nameAndDescriptionEditButton;
    }

    @Override
    public HasVisibility addTagButtonVisibility() {
        return addTagButton;
    }

    @Override
    public HasVisibility addLinkButtonVisibility() {
        return addLinkButton;
    }

    @Override
    public HasWidgets getInfoContainer() {
        return issueInfoContainer;
    }

    @Override
    public void setPrivateIssue( boolean isPrivate ) {
        if (isPrivate) {
            privacyIcon.setClassName( "fa-fw fas fa-lock text-danger" );
            privacyIcon.setAttribute( DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.PRIVACY_ICON_PRIVATE );
        } else {
            privacyIcon.setClassName( "fa-fw fas fa-unlock text-success" );
            privacyIcon.setAttribute( DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.PRIVACY_ICON_PUBLIC );
        }
    }

    @Override
    public HasWidgets getTagsContainer() {
        return tagsContainer;
    }

    @Override
    public void setCreatedBy(String value) {
        this.createdBy.setInnerHTML( value );
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
    public AbstractAttachmentUploader getFileUploader() {
        return fileUploader;
    }

    @Override
    public void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler) {
        fileUploader.setUploadHandler( handler );
    }

    @Override
    public HasVisibility addAttachmentUploaderVisibility() {
        return fileUploader;
    }

    @UiHandler("nameAndDescriptionEditButton")
    public void onEditNameAndDescriptionButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onNameAndDescriptionEditClicked();
        }
    }

    @UiHandler("backButton")
    public void onBackButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onBackClicked();
        }
    }

    @UiHandler("showEditViewButton")
    public void onShowEditViewModeButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onOpenEditViewClicked();
        }
    }

    @UiHandler("addTagButton")
    public void onAddTagButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onAddTagClicked(addTagButton);
        }
    }

    @UiHandler("copyNumber")
    public void onCopyNumberClick(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onCopyNumberClicked();
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

    @UiHandler("addSubtaskButton")
    public void onAddSubtaskButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onAddSubtaskClicked();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        privacyIcon.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.PRIVACY_ICON);
        copyNumber.ensureDebugId(DebugIds.ISSUE.COPY_NUMBER_BUTTON);
        backButton.ensureDebugId(DebugIds.ISSUE.BACK_BUTTON);
        showEditViewButton.ensureDebugId(DebugIds.ISSUE.SHOW_EDIT_BUTTON);
        addTagButton.ensureDebugId(DebugIds.ISSUE.TAGS_BUTTON);
        addLinkButton.ensureDebugId(DebugIds.ISSUE.LINKS_BUTTON);
        nameAndDescriptionEditButton.ensureDebugId(DebugIds.ISSUE.EDIT_NAME_AND_DESC_BUTTON);
        favoritesButton.ensureDebugId(DebugIds.ISSUE.FAVORITES_BUTTON);
        fileUploader.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_UPLOAD_BUTTON);
        addSubtaskButton.ensureDebugId(DebugIds.ISSUE.SUBTASK_BUTTON);
    }

    @UiField
    Lang lang;
    @UiField
    HTMLPanel root;
    @UiField
    Anchor copyNumber;
    @UiField
    SpanElement number;
    @UiField
    Element createdBy;
    @UiField
    HTMLPanel numberPanel;
    @UiField
    Element privacyIcon;
    @UiField
    HTMLPanel tagsContainer;
    @UiField
    HTMLPanel metaEditContainer;
    @Inject
    @UiField
    ButtonPanelAttachmentUploader fileUploader;
    @UiField
    HTMLPanel cardBody;
    @UiField
    HTMLPanel issueInfoContainer;
    @UiField
    Button backButton;
    @UiField
    Button showEditViewButton;
    @UiField
    Button nameAndDescriptionEditButton;
    @UiField
    Button favoritesButton;
    @UiField
    Element favoriteButtonIcon;
    @UiField
    Button addTagButton;
    @UiField
    Button addLinkButton;
    @Inject
    @UiField(provided = true)
    IssueNameWidget nameWidget;
    @UiField
    HTMLPanel linksContainer;
    @UiField
    HTMLPanel integrationLabel;
    @UiField
    Element integrationLabelName;
    @UiField
    Button addSubtaskButton;

    private AbstractIssueEditActivity activity;

    interface IssueEditViewUiBinder extends UiBinder<HTMLPanel, IssueEditView> {}
    private static IssueEditViewUiBinder ourUiBinder = GWT.create(IssueEditViewUiBinder.class);
}
