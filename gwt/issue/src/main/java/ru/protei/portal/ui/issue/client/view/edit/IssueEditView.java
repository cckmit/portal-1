package ru.protei.portal.ui.issue.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.makdown.MarkdownAreaWithPreview;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditActivity;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditView;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

/**
 * Вид создания и редактирования обращения
 */
public class IssueEditView extends Composite implements AbstractIssueEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
//        description.setRenderer((text, consumer) -> activity.renderMarkupText(text, consumer));
//        description.setDisplayPreviewHandler(isDisplay -> activity.onDisplayPreviewChanged(DESCRIPTION, isDisplay));

        copyNumber.getElement().setAttribute("title", lang.issueCopyNumber());
//        copyNumberAndName.getElement().setAttribute("title", lang.issueCopyNumberAndName());
    }

    @Override
    public void setActivity(AbstractIssueEditActivity activity) {
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

    //    @Override
//    public HasValue<String> name() {
//        return name;
//    }
//
//    @Override
//    public HasValue<String> description() {
//        return description;
//    }
//
    @Override
    public HasWidgets getLinksContainer() {
        return linksContainer;
    }

//    @Override
//    public HasValidable nameValidator() {
//        return nameValidator;
//    }

    @Override
    public HasWidgets getCommentsContainer() {
        return commentsContainer;
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
    public void setCaseNumber(Long caseNumber) {
        number.setText(lang.crmPrefix() + caseNumber);
        fileUploader.autoBindingToCase(En_CaseType.CRM_SUPPORT, caseNumber);
    }

    @Override
    public void setPrivateIssue( boolean isPrivate ) {
        if ( isPrivate ) {
            privacyIcon.setClassName("fas fa-lock text-danger m-l-10");
            privacyIcon.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.PRIVACY_ICON_PRIVATE);
        } else {
            privacyIcon.setClassName("fas fa-unlock text-success m-l-10");
            privacyIcon.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.PRIVACY_ICON_PUBLIC);
        }
    }

//    @Override
//    public void setNumber(Integer num) {
//        if (num == null) {
//            number.setText("");
//            return;
//        }
//
//        number.setText("CRM-" + num);
//    }

//    @Override
//    public void setDescriptionPreviewAllowed(boolean isPreviewAllowed) {
//        description.setDisplayPreview(isPreviewAllowed);
//    }

//    @Override
//    public void switchToRONameAndDescriptionView(boolean isRO) {
//        descriptionContainer.setVisible(!isRO);
//        nameContainer.setVisible(!isRO);
//
//        if (isRO) {
//            nameROLabel.removeClassName(UiConstants.Styles.HIDE);
//            descriptionRO.removeClassName(UiConstants.Styles.HIDE);
//        } else {
//            nameROLabel.addClassName(UiConstants.Styles.HIDE);
//            descriptionRO.addClassName(UiConstants.Styles.HIDE);
//        }
//    }
//
//    @Override
//    public void setDescriptionRO(String value) {
//        descriptionRO.setInnerHTML(value);
//    }
//
//
//    @Override
//    public void setNameRO( String value, String jiraUrl ) {
//        if (jiraUrl.isEmpty() || !value.startsWith("CLM")) {
//            this.nameROLabel.setInnerHTML(value);
//        }
//        else {
//            String idCLM = value.split(" ")[0];
//            String remainingName = "&nbsp;" + value.substring(idCLM.length());
//
//            AnchorElement jiraLink = DOM.createAnchor().cast();
//
//            jiraLink.setHref(jiraUrl + idCLM);
//            jiraLink.setTarget("_blank");
//            jiraLink.setInnerText(idCLM);
//
//            LabelElement nameWithoutLink = DOM.createLabel().cast();
//            nameWithoutLink.setInnerHTML(remainingName);
//
//            this.nameROLabel.setInnerHTML("");
//            this.nameROLabel.appendChild(jiraLink);
//            this.nameROLabel.appendChild(nameWithoutLink);
//        }
//    }

    @Override
    public HasWidgets getTagsContainer() {
        return tagsContainer;
    }

    @Override
    public void setCreatedBy(String value) {
        this.createdBy.setInnerHTML( value );
    }

//    @Override
//    public HasVisibility copyNumberAndNameVisibility() {
//        return copyNumberAndName;
//    }

    @Override
    public HasVisibility editNameAndDescriptionButtonVisibility() {
        return editNameAndDescriptionButton;
    }

//    @Override
//    public void setNameAndDescriptionButtonsPanelVisibility(boolean visible) {
//        if (visible) {
//            nameAndDescriptionButtonsPanel.removeClassName("hide");
//        } else {
//            nameAndDescriptionButtonsPanel.addClassName("hide");
//        }
//    }

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

//    @UiHandler("copyNumberAndName")
//    public void onCopyNumberAndNameClick(ClickEvent event) {
//        event.preventDefault();
//        if (activity != null) {
//            activity.onCopyNumberAndNameClicked();
//        }
//    }

    @UiHandler("editNameAndDescriptionButton")
    public void onEditNameAndDescriptionButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onEditNameAndDescriptionClicked(this);
        }
    }

//    @UiHandler("saveNameAndDescriptionButton")
//    public void onSaveNameAndDescriptionButtonClick(ClickEvent event) {
//        if (activity != null) {
//            activity.onSaveNameAndDescriptionClicked();
//        }
//    }
//
//    @UiHandler("cancelNameAndDescriptionButton")
//    public void onCancelNameAndDescriptionButtonClick(ClickEvent event) {
//        if (activity != null) {
//            activity.onEditNameAndDescriptionClicked();
//        }
//    }

//    @UiHandler( "backButton" )
//    public void onGoToIssuesClicked ( ClickEvent event) {
//        if ( activity != null ) {
//            activity.onGoToIssuesClicked();
//        }
//    }

    @UiHandler( "number" )
    public void onFullScreenClicked ( ClickEvent event) {
        event.preventDefault();

        if ( activity != null ) {
            activity.onFullScreenPreviewClicked();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        privacyIcon.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.PRIVACY_ICON);
//        number.ensureDebugId(DebugIds.ISSUE.NUMBER_INPUT);
        number.ensureDebugId(DebugIds.ISSUE_PREVIEW.FULL_SCREEN_BUTTON);
//        name.ensureDebugId(DebugIds.ISSUE.NAME_INPUT);
//        nameROLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.NAME_FIELD);
//        description.setEnsureDebugId(DebugIds.ISSUE.DESCRIPTION_INPUT);
//        descriptionRO.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.DESCRIPTION_FIELD);
        fileUploader.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_UPLOAD_BUTTON);
        attachmentContainer.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_LIST_CONTAINER);
        copyNumber.ensureDebugId(DebugIds.ISSUE.COPY_NUMBER_BUTTON);
//        copyNumberAndName.ensureDebugId(DebugIds.ISSUE.COPY_NUMBER_AND_NAME_BUTTON);

//        nameLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.NAME);
//        descriptionLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.INFO);
        attachmentsLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.ATTACHMENTS);
    }

    @UiField
    Lang lang;
    @UiField
    HTMLPanel root;
//    @UiField
//    TextBox name;
//    @UiField
//    MarkdownAreaWithPreview description;
    @UiField
    Anchor copyNumber;
//    @UiField
//    Anchor copyNumberAndName;
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
//    @UiField
//    LabelElement descriptionLabel;
    @UiField
    LabelElement attachmentsLabel;
    @UiField
    Anchor number;
    @UiField
    Element createdBy;
    @UiField
    HTMLPanel numberContainer;
    @UiField
    Element privacyIcon;
//    @UiField
//    HTMLPanel nameContainer;
//    @UiField
//    LabelElement nameLabel;
//    @UiField
//    HeadingElement nameROContainer;
//    @UiField
//    LabelElement nameROLabel;
//    @UiField
//    DivElement descriptionRO;
//    @UiField
//    HTMLPanel descriptionContainer;
    @UiField
    HTMLPanel linksContainer;
    @UiField
    HTMLPanel tagsContainer;
    @UiField
    HTMLPanel metaEditContainer;
    @UiField
    Button editNameAndDescriptionButton;
//    @UiField
//    Button saveNameAndDescriptionButton;
//    @UiField
//    Button cancelNameAndDescriptionButton;
//    @UiField
//    DivElement nameAndDescriptionButtonsPanel;
    @UiField
    HTMLPanel cardBody;
    @UiField
    HTMLPanel issueInfoContainer;


//    private HasValidable nameValidator = new HasValidable() {
//        @Override
//        public void setValid(boolean isValid) {
//            if ( isValid ) {
//                nameContainer.removeStyleName( HAS_ERROR );
//            } else {
//                nameContainer.addStyleName(HAS_ERROR);
//            }
//        }
//
//        @Override
//        public boolean isValid() {
//            return HelperFunc.isNotEmpty(name.getValue());
//        }
//    };

    private AbstractIssueEditActivity activity;


    interface IssueEditViewUiBinder extends UiBinder<HTMLPanel, IssueEditView> {}
    private static IssueEditViewUiBinder ourUiBinder = GWT.create(IssueEditViewUiBinder.class);
}