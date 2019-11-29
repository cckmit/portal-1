package ru.protei.portal.ui.issue.client.view.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.view.selector.ElapsedTimeTypeFormSelector;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.casemeta.CaseMetaView;
import ru.protei.portal.ui.common.client.widget.issueimportance.ImportanceFormSelector;
import ru.protei.portal.ui.common.client.widget.issuestate.IssueStateFormSelector;
import ru.protei.portal.ui.common.client.widget.makdown.MarkdownAreaWithPreview;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.InitiatorModel;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitFormSelector;
import ru.protei.portal.ui.common.client.widget.timefield.HasTime;
import ru.protei.portal.ui.common.client.widget.timefield.TimeLabel;
import ru.protei.portal.ui.common.client.widget.timefield.TimeTextBox;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.issue.client.activity.create.AbstractIssueCreateActivity;
import ru.protei.portal.ui.issue.client.activity.create.AbstractIssueCreateView;
import ru.protei.portal.ui.issue.client.view.meta.IssueMetaView;
import ru.protei.portal.ui.sitefolder.client.view.platform.widget.selector.PlatformFormSelector;

import java.util.Set;

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
    public HTMLPanel getIssueMetaViewContainer() {
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
            activity.onLocalClicked();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        privacyButton.ensureDebugId(DebugIds.ISSUE.PRIVACY_BUTTON);
        name.ensureDebugId(DebugIds.ISSUE.NAME_INPUT);
        caseMetaView.setEnsureDebugLinkId(DebugIds.ISSUE.LINKS_BUTTON);
        caseMetaView.setEnsureDebugIdLinkContainer(DebugIds.ISSUE.LINKS_CONTAINER);
        caseMetaView.setEnsureDebugIdLinkSelector(DebugIds.ISSUE.LINKS_TYPE_SELECTOR);
        caseMetaView.setEnsureDebugIdLinkTextBox(DebugIds.ISSUE.LINKS_INPUT);
        caseMetaView.setEnsureDebugIdLinkApply(DebugIds.ISSUE.LINKS_APPLY_BUTTON);
        caseMetaView.setEnsureDebugTagId(DebugIds.ISSUE.TAGS_BUTTON);
        caseMetaView.setEnsureDebugIdTagLabel(DebugIds.ISSUE.LABEL.TAGS);
        caseMetaView.setEnsureDebugIdTagContainer(DebugIds.ISSUE.TAGS_CONTAINER);
        description.setEnsureDebugId(DebugIds.ISSUE.DESCRIPTION_INPUT);
        fileUploader.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_UPLOAD_BUTTON);
        attachmentContainer.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_LIST_CONTAINER);
        saveButton.ensureDebugId(DebugIds.ISSUE.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.ISSUE.CANCEL_BUTTON);

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
    Button saveButton;
    @UiField
    Button cancelButton;

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
    HTMLPanel nameContainer;
    @UiField
    LabelElement nameLabel;
    @UiField
    HTMLPanel descriptionContainer;
    @UiField
    HTMLPanel issueMetaViewContainer;

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

    private AbstractIssueCreateActivity activity;

    interface IssueCreateViewUiBinder extends UiBinder<HTMLPanel, IssueCreateView> {
    }
    private static IssueCreateViewUiBinder ourUiBinder = GWT.create(IssueCreateViewUiBinder.class);
}