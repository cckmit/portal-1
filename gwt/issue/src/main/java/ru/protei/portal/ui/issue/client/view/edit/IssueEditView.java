package ru.protei.portal.ui.issue.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.casemeta.CaseMetaView;
import ru.protei.portal.ui.common.client.widget.makdown.MarkdownAreaWithPreview;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditActivity;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditView;
import ru.protei.portal.ui.issue.client.activity.meta.AbstractIssueMetaActivity;
import ru.protei.portal.ui.issue.client.activity.meta.AbstractIssueMetaView;
import ru.protei.portal.ui.issue.client.view.meta.IssueMetaView;

import java.util.Set;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

/**
 * Вид создания и редактирования обращения
 */
public class IssueEditView extends Composite implements AbstractIssueEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        description.setRenderer((text, consumer) -> activity.renderMarkupText(text, consumer));
        description.setDisplayPreviewHandler(isDisplay -> activity.onDisplayPreviewChanged(DESCRIPTION, isDisplay));

        copy.getElement().setAttribute("title", lang.issueCopyToClipboard());
        caseMetaView.addValueChangeHandler(event ->  activity.onCaseMetaChanged(event.getValue()) );
    }

    @Override
    public void setActivity(AbstractIssueEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setMetaActivity(AbstractIssueMetaActivity activity) {
        issueMeta.setMetaActivity(activity);
    }

    @Override
    public AbstractIssueMetaView getMetaView() {
        return issueMeta;
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
    public HasValue<Set<CaseLink>> links() {
        return linksHasValue;
    }

    @Override
    public HasValue<Set<CaseTag>> tags() {
        return tagsHasValue;
    }

    @Override
    public HasValidable nameValidator() {
        return nameValidator;
    }

    @Override
    public HasVisibility numberVisibility(){
        return numberLabel;
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
    public HasVisibility numberContainerVisibility() {
        return numberContainer;
    }

    @Override
    public void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler) {
        fileUploader.setUploadHandler(handler);
    }

    @Override
    public void setCaseNumber(Long caseNumber) {
        fileUploader.autoBindingToCase(En_CaseType.CRM_SUPPORT, caseNumber);
    }

    @Override
    public void setPrivacyIcon(Boolean isPrivate) {
        if ( isPrivate ) {
            privacyIcon.setClassName("fas fa-lock text-danger m-r-10");
            privacyIcon.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.PRIVACY_ICON_PRIVATE);
        } else {
            privacyIcon.setClassName("fas fa-unlock text-success m-r-10");
            privacyIcon.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.PRIVACY_ICON_PUBLIC);
        }
    }

    @Override
    public HasVisibility saveVisibility() {
        return saveButton;
    }

    @Override
    public void setNumber(Integer num) {
        if (num == null) {
            numberLabel.setText("");
            return;
        }

        numberLabel.setText("CRM-" + num);
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
    public void setTagsAddButtonEnabled(boolean enabled) {
        caseMetaView.setTagsAddButtonEnabled(enabled);
    }

    @Override
    public void setTagsEditButtonEnabled(boolean enabled) {
        caseMetaView.setTagsEditButtonEnabled(enabled);
    }

    @Override
    public void setDescriptionPreviewAllowed(boolean isPreviewAllowed) {
        description.setDisplayPreview(isPreviewAllowed);
    }

    @Override
    public void switchToRONameDescriptionView(boolean isRO) {
        descriptionContainer.setVisible(!isRO);
        nameContainer.setVisible(!isRO);

        if (isRO) {
            nameRO.removeClassName(UiConstants.Styles.HIDE);
            descriptionRO.removeClassName(UiConstants.Styles.HIDE);
        } else {
            nameRO.addClassName(UiConstants.Styles.HIDE);
            descriptionRO.addClassName(UiConstants.Styles.HIDE);
        }
    }

    @Override
    public void setDescriptionRO(String value) {
        descriptionRO.setInnerHTML(value);
    }


    @Override
    public void setNameRO( String value, String jiraUrl ) {
        if (jiraUrl.isEmpty() || !value.startsWith("CLM")) {
            this.nameRO.setInnerHTML(value);
        }
        else {
            String idCLM = value.split(" ")[0];
            String remainingName = "&nbsp;" + value.substring(idCLM.length());

            AnchorElement jiraLink = DOM.createAnchor().cast();

            jiraLink.setHref(jiraUrl + idCLM);
            jiraLink.setTarget("_blank");
            jiraLink.setInnerText(idCLM);

            LabelElement nameWithoutLink = DOM.createLabel().cast();
            nameWithoutLink.setInnerHTML(remainingName);

            this.nameRO.setInnerHTML("");
            this.nameRO.appendChild(jiraLink);
            this.nameRO.appendChild(nameWithoutLink);
        }
    }

    @Override
    public void setCreatedBy(String value) {
        this.createdBy.setInnerHTML( value );
    }

    @Override
    public HasVisibility copyVisibility() {
        return copy;
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
            activity.onLocalClicked();
        }
    }

    @UiHandler("copy")
    public void onCopyClick(ClickEvent event) {
        if (activity != null) {
            activity.onCopyClicked();
        }
    }

    @Override
    public void showComments(boolean isShow) {
        if (isShow)
            comments.removeClassName(UiConstants.Styles.HIDE);
        else
            comments.addClassName(UiConstants.Styles.HIDE);
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        privacyIcon.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.PRIVACY_ICON);
        privacyButton.ensureDebugId(DebugIds.ISSUE.PRIVACY_BUTTON);
        numberLabel.ensureDebugId(DebugIds.ISSUE.NUMBER_INPUT);
        name.ensureDebugId(DebugIds.ISSUE.NAME_INPUT);
        nameRO.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.NAME_FIELD);
        caseMetaView.setEnsureDebugLinkId(DebugIds.ISSUE.LINKS_BUTTON);
        caseMetaView.setEnsureDebugIdLinkContainer(DebugIds.ISSUE.LINKS_CONTAINER);
        caseMetaView.setEnsureDebugIdLinkSelector(DebugIds.ISSUE.LINKS_TYPE_SELECTOR);
        caseMetaView.setEnsureDebugIdLinkTextBox(DebugIds.ISSUE.LINKS_INPUT);
        caseMetaView.setEnsureDebugIdLinkApply(DebugIds.ISSUE.LINKS_APPLY_BUTTON);
        caseMetaView.setEnsureDebugTagId(DebugIds.ISSUE.TAGS_BUTTON);
        caseMetaView.setEnsureDebugIdTagLabel(DebugIds.ISSUE.LABEL.TAGS);
        caseMetaView.setEnsureDebugIdTagContainer(DebugIds.ISSUE.TAGS_CONTAINER);
        description.setEnsureDebugId(DebugIds.ISSUE.DESCRIPTION_INPUT);
        descriptionRO.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.DESCRIPTION_FIELD);
        fileUploader.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_UPLOAD_BUTTON);
        attachmentContainer.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_LIST_CONTAINER);
        saveButton.ensureDebugId(DebugIds.ISSUE.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.ISSUE.CANCEL_BUTTON);
        copy.ensureDebugId(DebugIds.ISSUE.COPY_TO_CLIPBOARD_BUTTON);

        nameLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.NAME);
        caseMetaView.setEnsureDebugIdLinkLabel(DebugIds.ISSUE.LABEL.LINKS);
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
    Anchor copy;
    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
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
    @Inject
    @UiField(provided = true)
    CaseMetaView caseMetaView;
    @UiField
    LabelElement descriptionLabel;
    @UiField
    LabelElement attachmentsLabel;
    @UiField
    InlineLabel numberLabel;
    @UiField
    Element createdBy;
    @UiField
    HTMLPanel numberContainer;
    @UiField
    Element privacyIcon;
    @UiField
    HTMLPanel nameContainer;
    @UiField
    LabelElement nameLabel;
    @UiField
    HeadingElement nameRO;
    @UiField
    DivElement descriptionRO;
    @UiField
    HTMLPanel descriptionContainer;
    @Inject
    @UiField(provided = true)
    IssueMetaView issueMeta;

    private HasValue<Set<CaseTag>> tagsHasValue = new HasValue<Set<CaseTag>>() {
        @Override public Set<CaseTag> getValue() { return caseMetaView.getTags(); }
        @Override public void setValue(Set<CaseTag> value) { caseMetaView.setTags(value); }
        @Override public void setValue(Set<CaseTag> value, boolean fireEvents) { caseMetaView.setTags(value); }
        @Override public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Set<CaseTag>> handler) { return null; }
        @Override public void fireEvent(GwtEvent<?> event) {}
    };

    private HasValue<Set<CaseLink>> linksHasValue = new HasValue<Set<CaseLink>>() {
        @Override public Set<CaseLink> getValue() { return caseMetaView.getLinks(); }
        @Override public void setValue(Set<CaseLink> value) { caseMetaView.setLinks(value); }
        @Override public void setValue(Set<CaseLink> value, boolean fireEvents) { caseMetaView.setLinks(value); }
        @Override public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Set<CaseLink>> handler) { return null; }
        @Override public void fireEvent(GwtEvent<?> event) {}
    };

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

    private AbstractIssueEditActivity activity;

    interface IssueEditViewUiBinder extends UiBinder<HTMLPanel, IssueEditView> {}
    private static IssueEditViewUiBinder ourUiBinder = GWT.create(IssueEditViewUiBinder.class);
}