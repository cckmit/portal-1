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
        importance.setDefaultValue(lang.selectIssueImportance());
        platform.setDefaultValue(lang.selectPlatform());
        company.setDefaultValue(lang.selectIssueCompany());
        company.showDeprecated(false);
        product.setDefaultValue(lang.selectIssueProduct());
        manager.setDefaultValue(lang.selectIssueManager());
        initiator.setDefaultValue(lang.selectIssueInitiator());
        initiator.setAddButtonText(lang.personCreateNew());
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
    public HasValue<En_CaseState> state() {
        return state;
    }

    @Override
    public HasValue<En_ImportanceLevel> importance() {
        return importance;
    }

    @Override
    public HasTime timeElapsedInput() {
        return timeElapsedInput;
    }

    @Override
    public HasValue<En_TimeElapsedType> timeElapsedType() {
        return timeElapsedType;
    }

    @Override
    public HasValue<EntityOption> company() {
        return company;
    }

    @Override
    public HasValue<PersonShortView> initiator() {
        return initiator;
    }

    @Override
    public HasValue<PersonShortView> manager() {
        return manager;
    }

    @Override
    public HasValue<ProductShortView> product() {
        return product;
    }

    @Override
    public HasValue<Boolean> isPrivate() {
        return privacyButton;
    }

    @Override
    public HasValue<Set<PersonShortView>> notifiers() {
        return notifiers;
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
    public HasValidable stateValidator() {
        return state;
    }

    @Override
    public HasValidable importanceValidator() {
        return importance;
    }

    @Override
    public HasVisibility timeElapsedContainerVisibility() {
        return timeElapsedContainer;
    }

    @Override
    public HasValidable companyValidator() {
        return company;
    }

    @Override
    public HasEnabled initiatorState() {
        return initiator;
    }

    @Override
    public HasEnabled platformState() {
        return platform;
    }

    @Override
    public void setSubscriptionEmails(String value) {
        subscriptions.setInnerText(value);
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
    public HasEnabled companyEnabled() {
        return company;
    }

    @Override
    public HasEnabled productEnabled() {
        return product;
    }

    @Override
    public HasEnabled managerEnabled() {
        return manager;
    }

    @Override
    public HasEnabled stateEnabled() {
        return state;
    }

    @Override
    public HasVisibility caseSubscriptionContainer() {
        return caseSubscriptionContainers;
    }

    @Override
    public HasVisibility privacyVisibility() {
        return privacyButton;
    }

    @Override
    public HasVisibility timeElapsedEditContainerVisibility() {
        return timeElapsedEditContainer;
    }

    @Override
    public HasEnabled saveEnabled() {
        return saveButton;
    }

    @Override
    public void setStateFilter(Selector.SelectorFilter<En_CaseState> filter) {
        state.setFilter(filter);
    }

    @Override
    public void initiatorUpdateCompany(Company company) {
        initiator.updateCompanies(InitiatorModel.makeCompanyIds(company));
    }

    @Override
    public void initiatorSelectorAllowAddNew(boolean isVisible) {
        initiator.setAddButtonVisible(isVisible);
    }

    @Override
    public void applyCompanyValueIfOneOption() {
        company.applyValueIfOneOption();
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
    public void setStateWorkflow(En_CaseStateWorkflow workflow) {
        state.setWorkflow(workflow);
    }

    @Override
    public void setDescriptionPreviewAllowed(boolean isPreviewAllowed) {
        description.setDisplayPreview(isPreviewAllowed);
    }

    @Override
    public HasValue<PlatformOption> platform() {
        return platform;
    }

    @Override
    public void setPlatformFilter(Selector.SelectorFilter<PlatformOption> filter){
        platform.setFilter(filter);
    }

    @Override
    public HasVisibility platformVisibility() {
        return platformContainer;
    }

    @UiHandler("company")
    public void onChangeCompany(ValueChangeEvent<EntityOption> event) {
        if (activity != null) {
            activity.onCompanyChanged();
        }
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

    @UiHandler("initiator")
    public void onAddContactEvent(AddEvent event) {
        activity.onCreateContactClicked();
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
        state.setEnsureDebugId(DebugIds.ISSUE.STATE_SELECTOR);
        importance.setEnsureDebugId(DebugIds.ISSUE.IMPORTANCE_SELECTOR);
        platform.setEnsureDebugId(DebugIds.ISSUE.PLATFORM_SELECTOR);
        company.setEnsureDebugId(DebugIds.ISSUE.COMPANY_SELECTOR);
        initiator.setEnsureDebugId(DebugIds.ISSUE.INITIATOR_SELECTOR);
        product.setEnsureDebugId(DebugIds.ISSUE.PRODUCT_SELECTOR);
        manager.setEnsureDebugId(DebugIds.ISSUE.MANAGER_SELECTOR);
        timeElapsedInput.ensureDebugId(DebugIds.ISSUE.TIME_ELAPSED_INPUT);
        description.setEnsureDebugId(DebugIds.ISSUE.DESCRIPTION_INPUT);
        notifiers.setAddEnsureDebugId(DebugIds.ISSUE.NOTIFIERS_SELECTOR_ADD_BUTTON);
        notifiers.setClearEnsureDebugId(DebugIds.ISSUE.NOTIFIERS_SELECTOR_CLEAR_BUTTON);
        notifiers.setItemContainerEnsureDebugId(DebugIds.ISSUE.NOTIFIERS_SELECTOR_ITEM_CONTAINER);
        notifiers.setLabelEnsureDebugId(DebugIds.ISSUE.NOTIFIERS_SELECTOR_LABEL);
        fileUploader.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_UPLOAD_BUTTON);
        attachmentContainer.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_LIST_CONTAINER);
        saveButton.ensureDebugId(DebugIds.ISSUE.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.ISSUE.CANCEL_BUTTON);

        nameLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.NAME);
        caseMetaView.setEnsureDebugIdLinkLabel(DebugIds.ISSUE.LABEL.LINKS);
        state.ensureLabelDebugId(DebugIds.ISSUE.LABEL.STATE);
        importance.ensureLabelDebugId(DebugIds.ISSUE.LABEL.IMPORTANCE);
        platform.ensureLabelDebugId(DebugIds.ISSUE.LABEL.PLATFORM);
        company.ensureLabelDebugId(DebugIds.ISSUE.LABEL.COMPANY);
        initiator.ensureLabelDebugId(DebugIds.ISSUE.LABEL.CONTACT);
        product.ensureLabelDebugId(DebugIds.ISSUE.LABEL.PRODUCT);
        manager.ensureLabelDebugId(DebugIds.ISSUE.LABEL.MANAGER);
        newIssueTimeElapsedLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.NEW_ISSUE_TIME_ELAPSED);
        descriptionLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.INFO);
        subscriptionsLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.SUBSCRIPTIONS);
        notifiersLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.NOTIFIERS);
        attachmentsLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.ATTACHMENTS);
        timeElapsedType.ensureLabelDebugId(DebugIds.ISSUE.LABEL.TIME_ELAPSED_TYPE);
        notifiers.ensureDebugId(DebugIds.ISSUE.NOTIFIERS_SELECTOR);
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

    @Inject
    @UiField(provided = true)
    IssueStateFormSelector state;

    @Inject
    @UiField(provided = true)
    ImportanceFormSelector importance;

    @Inject
    @UiField(provided = true)
    TimeTextBox timeElapsedInput;

    @Inject
    @UiField(provided = true)
    ElapsedTimeTypeFormSelector timeElapsedType;

    @Inject
    @UiField(provided = true)
    CompanyFormSelector company;

    @Inject
    @UiField(provided = true)
    PersonFormSelector initiator;

    @Inject
    @UiField(provided = true)
    DevUnitFormSelector product;

    @Inject
    @UiField(provided = true)
    EmployeeFormSelector manager;
    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector notifiers;
    @Inject
    @UiField(provided = true)
    PlatformFormSelector platform;
    @UiField
    HTMLPanel platformContainer;

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
    Element subscriptions;
    @UiField
    HTMLPanel caseSubscriptionContainers;
    @Inject
    @UiField(provided = true)
    CaseMetaView caseMetaView;
    @UiField
    HTMLPanel timeElapsedContainer;
    @UiField
    LabelElement newIssueTimeElapsedLabel;
    @UiField
    LabelElement descriptionLabel;
    @UiField
    LabelElement subscriptionsLabel;
    @UiField
    LabelElement notifiersLabel;
    @UiField
    LabelElement attachmentsLabel;
    @UiField
    HTMLPanel nameContainer;
    @UiField
    HTMLPanel timeElapsedEditContainer;
    @UiField
    LabelElement nameLabel;
    @UiField
    HTMLPanel descriptionContainer;

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