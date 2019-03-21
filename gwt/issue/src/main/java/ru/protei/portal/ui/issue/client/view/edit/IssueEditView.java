package ru.protei.portal.ui.issue.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.view.selector.ElapsedTimeTypeSelector;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.issuelinks.list.IssueLinks;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.selector.dict.ImportanceButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.InitiatorModel;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitButtonSelector;
import ru.protei.portal.ui.common.client.widget.timefield.HasTime;
import ru.protei.portal.ui.common.client.widget.timefield.TimeLabel;
import ru.protei.portal.ui.common.client.widget.timefield.TimeTextBox;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditActivity;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditView;
import ru.protei.portal.ui.issue.client.widget.state.buttonselector.IssueStatesButtonSelector;

import java.util.Set;


/**
 * Вид создания и редактирования обращения
 */
public class IssueEditView extends Composite implements AbstractIssueEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        state.setDefaultValue(lang.selectIssueState());
        importance.setDefaultValue(lang.selectIssueImportance());
        company.setDefaultValue(lang.selectIssueCompany());
        product.setDefaultValue(lang.selectIssueProduct());
        manager.setDefaultValue(lang.selectIssueManager());
        initiator.setDefaultValue(lang.selectIssueInitiator());
        initiator.setAddButtonText(lang.personCreateNew());
    }

    @Override
    protected void onAttach() {
        super.onAttach();
    }

    @Override
    public void setActivity(AbstractIssueEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasText description() {
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
    public HasTime timeElapsedLabel() {
        return timeElapsed;
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
    public HasValue<Boolean> isLocal() {
        return local;
    }

    @Override
    public HasValue<Set<PersonShortView>> notifiers() {
        return notifiers;
    }

    @Override
    public HasValue<Set<CaseLink>> links() {
        return links;
    }

    @Override
    public HasValidable nameValidator() {
        return name;
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
    public HasValidable companyValidator() { return company; }

    @Override
    public HasValidable initiatorValidator() { return initiator; }

    @Override
    public HasValidable productValidator() {
        return product;
    }

    @Override
    public HasValidable managerValidator() {
        return manager;
    }

    @Override
    public HasEnabled initiatorState() {
        return initiator;
    }

    @Override
    public HasVisibility numberVisibility(){
        return number;
    }

    @Override
    public HasValue<Integer> number(){
        return number;
    }

    @Override
    public void setSubscriptionEmails(String value) {
        subscriptions.setInnerText(value);
    }

    @Override
    public HasWidgets getCommentsContainer() {
        return commentsContainer;
    }

    @Override
    public HasAttachments attachmentsContainer(){
        return attachmentContainer;
    }

    @Override
    public void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler){
        fileUploader.setUploadHandler(handler);
    }

    @Override
    public void setCaseNumber(Long caseNumber) {
        fileUploader.autoBindingToCase(En_CaseType.CRM_SUPPORT, caseNumber);
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
        return new HasVisibility() {
            @Override
            public boolean isVisible() {
                return local.isVisible();
            }

            @Override
            public void setVisible( boolean b ) {
                local.setVisible( b );
            }
        };
    }

    @Override
    public HasVisibility timeElapsedLabelVisibility() {
        return timeElapsed;
    }

    @Override
    public HasVisibility timeElapsedInputVisibility() {
        return timeElapsedInput;
    }

    @Override
    public void setTimeElapseTypeVisibility(boolean isVisible) {
        timeElapsedType.setVisible( isVisible );
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

    @UiHandler( "company" )
    public void onChangeCompany( ValueChangeEvent< EntityOption > event ){
        if ( activity != null ) {
            activity.onCompanyChanged();
        }
    }

    @UiHandler( "saveButton" )
    public void onSaveClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onSaveClicked();
        }
    }
    @UiHandler( "cancelButton" )
    public void onCancelClicked( ClickEvent event ) {
        if ( activity != null ) {
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

    @UiHandler("local")
    public void onLocalClick(ClickEvent event) {
        if (activity != null) {
            activity.onLocalClicked();
        }
    }

    @Override
    public void showComments(boolean isShow) {
        if(isShow)
            comments.removeClassName(UiConstants.Styles.HIDE);
        else
            comments.addClassName(UiConstants.Styles.HIDE);
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        local.ensureDebugId(DebugIds.ISSUE.PRIVACY_BUTTON);
        number.ensureDebugId(DebugIds.ISSUE.NUMBER_INPUT);
        name.ensureDebugId(DebugIds.ISSUE.NAME_INPUT);
        links.setEnsureDebugId(DebugIds.ISSUE.LINKS_BUTTON);
        links.setEnsureDebugIdContainer(DebugIds.ISSUE.LINKS_CONTAINER);
        links.setEnsureDebugIdSelector(DebugIds.ISSUE.LINKS_TYPE_SELECTOR);
        links.setEnsureDebugIdTextBox(DebugIds.ISSUE.LINKS_INPUT);
        links.setEnsureDebugIdApply(DebugIds.ISSUE.LINKS_APPLY_BUTTON);
        links.setEnsureDebugIdErrorLabel(DebugIds.ISSUE.LINKS_ERROR_LABEL);
        state.setEnsureDebugId(DebugIds.ISSUE.STATE_SELECTOR);
        importance.setEnsureDebugId(DebugIds.ISSUE.IMPORTANCE_SELECTOR);
        company.setEnsureDebugId(DebugIds.ISSUE.COMPANY_SELECTOR);
        initiator.setEnsureDebugId(DebugIds.ISSUE.INITIATOR_SELECTOR);
        product.setEnsureDebugId(DebugIds.ISSUE.PRODUCT_SELECTOR);
        manager.setEnsureDebugId(DebugIds.ISSUE.MANAGER_SELECTOR);
        timeElapsed.ensureDebugId(DebugIds.ISSUE.TIME_ELAPSED_LABEL);
        timeElapsedInput.ensureDebugId(DebugIds.ISSUE.TIME_ELAPSED_INPUT);
        description.ensureDebugId(DebugIds.ISSUE.DESCRIPTION_INPUT);
        notifiers.setAddEnsureDebugId(DebugIds.ISSUE.NOTIFIERS_SELECTOR_ADD_BUTTON);
        notifiers.setClearEnsureDebugId(DebugIds.ISSUE.NOTIFIERS_SELECTOR_CLEAR_BUTTON);
        fileUploader.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_UPLOAD_BUTTON);
        attachmentContainer.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_LIST_CONTAINER);
        saveButton.ensureDebugId(DebugIds.ISSUE.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.ISSUE.CANCEL_BUTTON);

        nameLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.NAME);
        links.setEnsureDebugIdLabel(DebugIds.ISSUE.LABEL.LINKS);
        stateLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.STATE);
        importanceLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.IMPORTANCE);
        companyLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.COMPANY);
        initiatorLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.CONTACT);
        productLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.PRODUCT);
        managerLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.MANAGER);
        timeElapsedLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.TIME_ELAPSED);
        descriptionLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.INFO);
        subscriptionsLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.SUBSCRIPTIONS);
        notifiersLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.NOTIFIERS);
        attachmentsLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.ATTACHMENTS);
    }

    @UiField
    HTMLPanel root;

    @UiField
    ValidableTextBox name;

    @UiField
    TextArea description;

    @UiField
    ToggleButton local;

    @UiField
    IntegerBox number;

    @Inject
    @UiField(provided = true)
    IssueStatesButtonSelector state;

    @Inject
    @UiField(provided = true)
    ImportanceButtonSelector importance;

    @Inject
    @UiField(provided = true)
    TimeLabel timeElapsed;

    @Inject
    @UiField(provided = true)
    TimeTextBox timeElapsedInput;

    @Inject
    @UiField(provided = true)
    ElapsedTimeTypeSelector timeElapsedType;

    @Inject
    @UiField(provided = true)
    CompanySelector company;

    @Inject
    @UiField(provided = true)
    PersonButtonSelector initiator;

    @Inject
    @UiField(provided = true)
    DevUnitButtonSelector product;

    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector manager;

    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector notifiers;

    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @Inject
    @UiField
    Lang lang;
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
    DivElement subscriptions;
    @UiField
    HTMLPanel nameInputGroupContainer;
    @UiField
    HTMLPanel nameContainer;
    @UiField
    HTMLPanel caseSubscriptionContainers;
    @Inject
    @UiField(provided = true)
    IssueLinks links;
    @UiField
    HTMLPanel timeElapsedContainer;

    @UiField
    LabelElement nameLabel;
    @UiField
    LabelElement stateLabel;
    @UiField
    LabelElement importanceLabel;
    @UiField
    LabelElement companyLabel;
    @UiField
    LabelElement initiatorLabel;
    @UiField
    LabelElement productLabel;
    @UiField
    LabelElement managerLabel;
    @UiField
    LabelElement timeElapsedLabel;
    @UiField
    LabelElement descriptionLabel;
    @UiField
    LabelElement subscriptionsLabel;
    @UiField
    LabelElement notifiersLabel;
    @UiField
    LabelElement attachmentsLabel;

    private AbstractIssueEditActivity activity;

    interface IssueEditViewUiBinder extends UiBinder<HTMLPanel, IssueEditView> {}
    private static IssueEditViewUiBinder ourUiBinder = GWT.create(IssueEditViewUiBinder.class);
}