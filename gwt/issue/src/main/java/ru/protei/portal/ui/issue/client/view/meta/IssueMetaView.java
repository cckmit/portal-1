package ru.protei.portal.ui.issue.client.view.meta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.dict.En_WorkTrigger;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.view.*;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.view.selector.ElapsedTimeTypeFormSelector;
import ru.protei.portal.ui.common.client.widget.issueimportance.ImportanceFormSelector;
import ru.protei.portal.ui.common.client.widget.issueimportance.ImportanceModel;
import ru.protei.portal.ui.common.client.widget.issuestate.IssueStateFormSelector;
import ru.protei.portal.ui.common.client.widget.issuestate.StateModel;
import ru.protei.portal.ui.common.client.widget.jirasla.JiraSLASelector;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyModel;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonModel;
import ru.protei.portal.ui.common.client.widget.selector.plan.selector.PlanMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.platform.PlatformFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.worktrigger.WorkTriggerFormSelector;
import ru.protei.portal.ui.common.client.widget.timefield.HasTime;
import ru.protei.portal.ui.common.client.widget.timefield.TimeLabel;
import ru.protei.portal.ui.common.client.widget.timefield.TimeTextBox;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.issue.client.activity.meta.AbstractIssueMetaActivity;
import ru.protei.portal.ui.issue.client.activity.meta.AbstractIssueMetaView;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.setOf;
import static ru.protei.portal.ui.common.client.util.ClientTransliterationUtils.transliterateNotifiers;
import static ru.protei.portal.ui.common.client.util.ClientTransliterationUtils.transliteration;

public class IssueMetaView extends Composite implements AbstractIssueMetaView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        company.setAsyncModel( companyModel );
        initiator.setAsyncModel( initiatorModel );
        manager.setAsyncModel( managerModel );
        state.setStateModel( stateModel );
        importance.setAsyncModel( importanceModel );
        notifiers.setItemRenderer( PersonShortView::getName );
        initView();

        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractIssueMetaActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<CaseState> state( ) {
        return state;
    }

    @Override
    public HasValue<ImportanceLevel> importance( ) {
        return importance;
    }

    @Override
    public void fillImportanceOptions(List<ImportanceLevel> options) {
        importanceModel.fillOptions(options);
    }

    @Override
    public HasValue<ProductShortView> product() {
        return product;
    }

    @Override
    public void setManager( PersonShortView manager ) {
        if (manager != null) manager.setName(transliteration(manager.getName()));
        this.manager.setValue(manager);
    }

    @Override
    public PersonShortView getManager() {
        return manager.getValue();
    }

    @Override
    public void setCompany( Company company ) {
        EntityOption companyValue = EntityOption.fromCompany(company);
        if (companyValue != null) companyValue.setDisplayText(transliteration(companyValue.getDisplayText()));
        this.company.setValue(companyValue);
    }

    @Override
    public Company getCompany() {
        return Company.fromEntityOption(company.getValue());
    }

    @Override
    public void setCompanyModel(AsyncSelectorModel companyModel) {
        company.setAsyncModel(companyModel);
    }

    @Override
    public void setManagerCompanyModel(AsyncSelectorModel companyModel) {
        managerCompany.setAsyncModel(companyModel);
    }

    @Override
    public void setManagerCompany(EntityOption managerCompany) {
        if (managerCompany != null) {
            managerCompany.setDisplayText(transliteration(managerCompany.getDisplayText()));
        }

        this.managerCompany.setValue(managerCompany);
    }

    @Override
    public EntityOption getManagerCompany() {
        return this.managerCompany.getValue();
    }

    @Override
    public void setManagerMandatory(boolean isMandatory) {
        manager.setMandatory(isMandatory);
    }

    @Override
    public void setCaseMetaNotifiers( Set<Person> caseObjectMetaNotifiers) {
        notifiers.setValue(transliterateNotifiers(caseObjectMetaNotifiers));
    }

    @Override
    public Set<Person> getCaseMetaNotifiers() {
        return notifiers.getValue().stream().map(Person::fromPersonShortView).collect(Collectors.toSet());
    }

    @Override
    public void setCaseMetaJira(CaseObjectMetaJira caseObjectMetaJira) {
        jiraSlaSelector.setValue(caseObjectMetaJira);
    }

    @Override
    public HasValue<CaseObjectMetaJira> jiraSlaSelector() {
        return jiraSlaSelector;
    }

    @Override
    public void setSubscriptionEmails(String value) {
        subscriptions.setInnerText(value);
    }

    @Override
    public void initiatorSelectorAllowAddNew(boolean isVisible) {
        initiator.setAddButtonVisible(isVisible);
    }

    private static final Logger log = Logger.getLogger( IssueMetaView.class.getName() );
    @Override
    public void setInitiatorFilter(Long companyId) {
        initiatorModel.updateCompanies( null, setOf(companyId) );
    }

    @Override
    public void updateManagersCompanyFilter(Long managerCompanyId) {
        managerModel.updateCompanies( null, setOf(managerCompanyId) );
    }

    @Override
    public void setStateFilter(Selector.SelectorFilter<CaseState> filter) {
        state.setFilter(filter);
    }

    @Override
    public void setPlatformFilter(Selector.SelectorFilter<PlatformOption> filter) {
        platform.setFilter(filter);
    }

    @Override
    public void setTimeElapsed(Long timeElapsedValue) {
        timeElapsed.setTime(Objects.equals(0L, timeElapsedValue) ? null : timeElapsedValue);
        timeElapsedInput.setTime(Objects.equals(0L, timeElapsedValue) ? null : timeElapsedValue);
    }

    @Override
    public Long getTimeElapsed() {
        return timeElapsedInput.getTime();
    }

    @Override
    public void setTimeElapsedType(En_TimeElapsedType timeElapsedType) {
        this.timeElapsedType.setValue(timeElapsedType);
    }

    @Override
    public void setInitiator(Person initiator) {
        PersonShortView initiatorValue = toFullNameShortView(initiator);
        if (initiatorValue != null) initiatorValue.setName( transliteration( initiatorValue.getName() ) );
        this.initiator.setValue(initiatorValue);
    }

    @Override
    public Person getInitiator() {
        return Person.fromPersonFullNameShortView(initiator.getValue());
    }

    @Override
    public HasValue< PlatformOption> platform() {
        return platform;
    }

    @Override
    public void setStateWorkflow(En_CaseStateWorkflow workflow) {
        stateModel.setWorkflow(workflow);
    }

    @Override
    public HasVisibility timeElapsedHeaderVisibility() {
        return timeElapsedHeader;
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
    public HasValidable companyValidator() {
        return company;
    }

    @Override
    public HasValidable initiatorValidator() {
        return initiator;
    }

    @Override
    public HasValidable managerValidator() {
        return manager;
    }

    @Override
    public HasValidable productValidator() {
        return product;
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
    public HasEnabled importanceEnabled() {
        return importance;
    }

    @Override
    public HasEnabled caseMetaNotifiersEnabled() {
        return notifiers;
    }

    @Override
    public HasEnabled initiatorEnabled() {
        return initiator;
    }

    @Override
    public HasEnabled platformEnabled() {
        return platform;
    }

    @Override
    public HasEnabled caseMetaJiraEnabled() {
        return jiraSlaSelector;
    }

    @Override
    public HasVisibility caseSubscriptionContainer() {
        return caseSubscriptionContainers;
    }

    @Override
    public HasVisibility timeElapsedContainerVisibility() {
        return timeElapsedContainer;
    }

    @Override
    public HasVisibility timeElapsedEditContainerVisibility() {
        return timeElapsedEditContainer;
    }

    @Override
    public HasVisibility platformVisibility() {
        return platform;
    }

    @Override
    public void setInitiatorBorderBottomVisible(boolean isVisible) {
        initiatorContainer.setStyleName("add-border-bottom", isVisible);
    }

    @Override
    public void setProductBorderBottomVisible(boolean isVisible) {
        productContainer.setStyleName("add-border-bottom", isVisible);
    }

    @Override
    public HasVisibility jiraSlaSelectorVisibility() {
        return jiraSlaSelector;
    }

    @Override
    public HasValue<En_TimeElapsedType> timeElapsedType() {
        return timeElapsedType;
    }

    @Override
    public void setJiraInfoLink(String link) {
        jiraSlaSelector.setJiraInfoLink(link);
    }

    @Override
    public HasTime slaReactionTime() {
        return slaReactionTime;
    }

    @Override
    public HasTime slaTemporarySolutionTime() {
        return slaTemporarySolutionTime;
    }

    @Override
    public HasTime slaFullSolutionTime() {
        return slaFullSolutionTime;
    }

    @Override
    public HasVisibility slaContainerVisibility() {
        return slaContainer;
    }

    @Override
    public void setValuesContainerWarning(boolean isWarning) {
        slaTimesContainer.setStyleName("b-warning-light", isWarning);
    }

    @Override
    public void setSlaTimesContainerTitle(String title) {
        slaTimesContainer.setTitle(title);
    }

    @Override
    public HasVisibility pauseDateContainerVisibility() {
        return pauseDateContainer;
    }

    @Override
    public HasValue<Date> pauseDate() {
        return pauseDate;
    }

    @Override
    public void setPauseDateValid(boolean isValid) {
        pauseDate.markInputValid(isValid);
    }

    @Override
    public HasEnabled managerCompanyEnabled() {
        return managerCompany;
    }

    @Override
    public void updateProductsByPlatformIds(Set<Long> platformIds) {
        product.setPlatformIds(platformIds);
    }

    @Override
    public void setProductModel(ProductModel productModel) {
        product.setAsyncProductModel(productModel);
    }

    @Override
    public void setProductMandatory(boolean isProductMandatory) {
        product.setMandatory(isProductMandatory);
    }

    @Override
    public void setPlanCreatorId(Long creatorId) {
        plans.setCreatorId(creatorId);
    }

    @Override
    public HasValue<Set<PlanOption>> ownerPlans() {
        return plans;
    }

    @Override
    public HasVisibility ownerPlansContainerVisibility() {
        return ownerPlansContainer;
    }

    @Override
    public HasVisibility otherPlansContainerVisibility() {
        return otherPlansContainer;
    }

    @Override
    public void setOtherPlans(String otherPlans) {
        this.otherPlans.setInnerText(otherPlans);
    }

    @Override
    public void setPlansLabelVisible(boolean isVisible) {
        if (isVisible) {
            plansLabel.removeClassName(UiConstants.Styles.HIDE);
        } else {
            plansLabel.addClassName(UiConstants.Styles.HIDE);
        }
    }

    @Override
    public void setAutoCloseVisible(boolean isVisible) {
        if (isVisible) {
            autoCloseContainer.getElement().getParentElement().removeClassName(UiConstants.Styles.HIDE);
            deadlineContainer.getElement().getParentElement().removeClassName(UiConstants.Styles.FULL_VIEW);
            deadlineContainer.getElement().getParentElement().setClassName(UiConstants.Styles.SHORT_VIEW);
        } else {
            autoCloseContainer.getElement().getParentElement().addClassName(UiConstants.Styles.HIDE);
            deadlineContainer.getElement().getParentElement().addClassName(UiConstants.Styles.FULL_VIEW);
            deadlineContainer.getElement().getParentElement().removeClassName(UiConstants.Styles.SHORT_VIEW);
        }
    }

    @Override
    public HasValue<Boolean> autoClose() {
        return autoClose;
    }

    @Override
    public HasVisibility deadlineContainerVisibility() {
        return deadlineContainer;
    }

    @Override
    public HasValue<Date> deadline() {
        return deadline;
    }

    @Override
    public boolean isDeadlineEmpty() {
        return HelperFunc.isEmpty(deadline.getInputValue());
    }

    @Override
    public void setDeadlineValid(boolean isValid) {
        deadline.markInputValid(isValid);
    }

    @Override
    public HasVisibility workTriggerVisibility() {
        return workTriggerContainer;
    }

    @Override
    public HasValue<En_WorkTrigger> workTrigger() {
        return workTrigger;
    }

    private void initView() {
        importance.setDefaultValue(lang.selectIssueImportance());
        platform.setDefaultValue(lang.selectPlatform());
        company.setDefaultValue(lang.selectIssueCompany());
        managerCompany.setDefaultValue(lang.selectIssueCompany());
        companyModel.showDeprecated(false);
        product.setDefaultValue(lang.selectIssueProduct());
        manager.setDefaultValue(lang.selectIssueManager());
        initiator.setDefaultValue(lang.selectIssueInitiator());
        initiator.setAddButtonText(lang.personCreateNew());
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) return;
        state.setEnsureDebugId(DebugIds.ISSUE.STATE_SELECTOR);
        pauseDate.setEnsureDebugId(DebugIds.ISSUE.PAUSE_DATE_CONTAINER);
        importance.setEnsureDebugId(DebugIds.ISSUE.IMPORTANCE_SELECTOR);
        platform.setEnsureDebugId(DebugIds.ISSUE.PLATFORM_SELECTOR);
        company.setEnsureDebugId(DebugIds.ISSUE.COMPANY_SELECTOR);
        managerCompany.setEnsureDebugId(DebugIds.ISSUE.MANAGER_COMPANY_SELECTOR);
        initiator.setEnsureDebugId(DebugIds.ISSUE.INITIATOR_SELECTOR);
        product.setEnsureDebugId(DebugIds.ISSUE.PRODUCT_SELECTOR);
        manager.setEnsureDebugId(DebugIds.ISSUE.MANAGER_SELECTOR);
        timeElapsed.ensureDebugId(DebugIds.ISSUE.TIME_ELAPSED);
        timeElapsedInput.ensureDebugId(DebugIds.ISSUE.TIME_ELAPSED_INPUT);
        notifiers.setAddEnsureDebugId(DebugIds.ISSUE.NOTIFIERS_SELECTOR_ADD_BUTTON);
        notifiers.setClearEnsureDebugId(DebugIds.ISSUE.NOTIFIERS_SELECTOR_CLEAR_BUTTON);
        notifiers.setItemContainerEnsureDebugId(DebugIds.ISSUE.NOTIFIERS_SELECTOR_ITEM_CONTAINER);
        notifiers.setLabelEnsureDebugId(DebugIds.ISSUE.NOTIFIERS_SELECTOR_LABEL);
        state.ensureLabelDebugId(DebugIds.ISSUE.LABEL.STATE);
        importance.ensureLabelDebugId(DebugIds.ISSUE.LABEL.IMPORTANCE);
        platform.ensureLabelDebugId(DebugIds.ISSUE.LABEL.PLATFORM);
        company.ensureLabelDebugId(DebugIds.ISSUE.LABEL.COMPANY);
        managerCompany.ensureLabelDebugId(DebugIds.ISSUE.LABEL.MANAGER_COMPANY);
        initiator.ensureLabelDebugId(DebugIds.ISSUE.LABEL.CONTACT);
        product.ensureLabelDebugId(DebugIds.ISSUE.LABEL.PRODUCT);
        manager.ensureLabelDebugId(DebugIds.ISSUE.LABEL.MANAGER);
        timeElapsedLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.TIME_ELAPSED);
        newIssueTimeElapsedLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.NEW_ISSUE_TIME_ELAPSED);
        subscriptionsLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.SUBSCRIPTIONS);
        notifiersLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.NOTIFIERS);
        timeElapsedType.ensureLabelDebugId(DebugIds.ISSUE.LABEL.TIME_ELAPSED_TYPE);
        notifiers.ensureDebugId(DebugIds.ISSUE.NOTIFIERS_SELECTOR);
        plans.setAddEnsureDebugId(DebugIds.ISSUE.PLANS_SELECTOR_ADD_BUTTON);
        plans.setClearEnsureDebugId(DebugIds.ISSUE.PLANS_SELECTOR_CLEAR_BUTTON);
        plans.setItemContainerEnsureDebugId(DebugIds.ISSUE.PLANS_SELECTOR_ITEM_CONTAINER);
        plans.setLabelEnsureDebugId(DebugIds.ISSUE.PLANS_SELECTOR_LABEL);
        plans.ensureDebugId(DebugIds.ISSUE.PLANS_SELECTOR);
        // TODO уточнить нужен ли DebugId на чекбокс.
        deadline.setEnsureDebugId(DebugIds.ISSUE.DEADLINE_CONTAINER);
        workTrigger.setEnsureDebugId(DebugIds.ISSUE.WORK_TRIGGER_SELECTOR);
    }

    @UiHandler("state")
    public void onStateChanged(ValueChangeEvent<CaseState> event) {
        activity.onStateChange();
    }

    @UiHandler("importance")
    public void onImportanceChanged(ValueChangeEvent<ImportanceLevel> event) {
        activity.onImportanceChanged();
    }

    @UiHandler("product")
    public void onProductChanged(ValueChangeEvent<ProductShortView> event) {
        activity.onProductChanged();
    }

    @UiHandler("manager")
    public void onManagerChanged(ValueChangeEvent<PersonShortView> event) {
        activity.onManagerChanged();
    }

    @UiHandler("company")
    public void onInitiatorCompanyChanged(ValueChangeEvent<EntityOption> event) {
        if (activity != null) activity.onInitiatorCompanyChanged();
    }

    @UiHandler("initiator")
    public void onAddContactEvent(AddEvent event) {
        if (activity == null) return;
        activity.onCreateContactClicked();
    }

    @UiHandler("initiator")
    public void onInitiatorChanged(ValueChangeEvent<PersonShortView> event) {
        activity.onInitiatorChanged();
    }

    @UiHandler("platform")
    public void onPlatformChanged(ValueChangeEvent<PlatformOption> event) {
        activity.onPlatformChanged();
    }

    @UiHandler("timeElapsedInput")
    public void onTimeElapsedChanged(ValueChangeEvent<String> event) {
        activity.onTimeElapsedChanged();
    }

    @UiHandler("notifiers")
    public void onNotifiersChanged(ValueChangeEvent<Set<PersonShortView>> event) {
        activity.onCaseMetaNotifiersChanged( );
    }

    @UiHandler("jiraSlaSelector")
    public void onJiraSlaChanged(ValueChangeEvent<CaseObjectMetaJira> event) {
        activity.onCaseMetaJiraChanged();
    }

    @UiHandler("pauseDate")
    public void onPauseDateChanged(ValueChangeEvent<Date> event) {
        activity.onPauseDateChanged();
    }

    @UiHandler("managerCompany")
    public void onManagerCompanyChanged(ValueChangeEvent<EntityOption> event) {
        activity.onManagerCompanyChanged();
    }

    @UiHandler("plans")
    public void onPlanChanged(ValueChangeEvent<Set<PlanOption>> event) {
        activity.onPlansChanged();
    }

    @UiHandler("autoClose")
    public void onAutoCloseChanged(ClickEvent event) {
        if (activity != null) {
            activity.onAutoCloseChanged();
        }
    }

    @UiHandler("deadline")
    public void onDeadlineChanged(ValueChangeEvent<Date> event) {
        activity.onDeadlineChanged();
    }

    @UiHandler("workTrigger")
    public void onWorkTriggerChanged(ValueChangeEvent<En_WorkTrigger> event) {
        activity.onWorkTriggerChanged();
    }

    @UiField
    @Inject
    Lang lang;
    @Inject
    @UiField(provided = true)
    IssueStateFormSelector state;
    @Inject
    @UiField(provided = true)
    SinglePicker pauseDate;
    @UiField
    HTMLPanel pauseDateContainer;
    @Inject
    @UiField(provided = true)
    ImportanceFormSelector importance;
    @Inject
    @UiField(provided = true)
    DevUnitFormSelector product;
    @Inject
    @UiField(provided = true)
    PlanMultiSelector plans;
    @UiField
    HTMLPanel ownerPlansContainer;
    @UiField
    LabelElement plansLabel;
    @UiField
    HTMLPanel productContainer;
    @Inject
    @UiField(provided = true)
    CompanyFormSelector managerCompany;
    @Inject
    @UiField(provided = true)
    PersonFormSelector manager;
    @Inject
    @UiField(provided = true)
    CompanyFormSelector company;
    @Inject
    @UiField(provided = true)
    PersonFormSelector initiator;
    @UiField
    HTMLPanel initiatorContainer;
    @Inject
    @UiField(provided = true)
    PlatformFormSelector platform;
    @Inject
    @UiField(provided = true)
    JiraSLASelector jiraSlaSelector;
    @UiField
    HTMLPanel timeElapsedContainer;
    @UiField
    HTMLPanel timeElapsedHeader;
    @UiField
    LabelElement timeElapsedLabel;
    @Inject
    @UiField(provided = true)
    TimeLabel timeElapsed;
    @UiField
    HTMLPanel timeElapsedEditContainer;
    @Inject
    @UiField(provided = true)
    TimeTextBox timeElapsedInput;
    @Inject
    @UiField(provided = true)
    ElapsedTimeTypeFormSelector timeElapsedType;
    @UiField
    LabelElement subscriptionsLabel;
    @UiField
    Element subscriptions;
    @UiField
    Element otherPlans;
    @UiField
    HTMLPanel otherPlansContainer;
    @UiField
    HTMLPanel slaContainer;
    @Inject
    @UiField(provided = true)
    TimeLabel slaReactionTime;
    @Inject
    @UiField(provided = true)
    TimeLabel slaTemporarySolutionTime;
    @Inject
    @UiField(provided = true)
    TimeLabel slaFullSolutionTime;
    @UiField
    HTMLPanel slaTimesContainer;
    @UiField
    HTMLPanel caseSubscriptionContainers;
    @UiField
    LabelElement newIssueTimeElapsedLabel;
    @UiField
    LabelElement notifiersLabel;
    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector notifiers;

    @UiField
    HTMLPanel autoCloseContainer;
    @UiField
    CheckBox autoClose;
    @UiField
    HTMLPanel deadlineContainer;
    @Inject
    @UiField(provided = true)
    SinglePicker deadline;

    @UiField
    HTMLPanel workTriggerContainer;
    @Inject
    @UiField(provided = true)
    WorkTriggerFormSelector workTrigger;


    private PersonShortView toFullNameShortView(Person person){
        if(person==null) return null;
        return new PersonShortView( person.getDisplayName(), person.getId(), person.isFired() );
    }

    @Inject
    CompanyModel companyModel;
    @Inject
    PersonModel managerModel;
    @Inject
    PersonModel initiatorModel;
    @Inject
    ImportanceModel importanceModel;
    @Inject
    StateModel stateModel;

    private AbstractIssueMetaActivity activity;

    interface IssueMetaViewUiBinder extends UiBinder<HTMLPanel, IssueMetaView> {}
    private static IssueMetaViewUiBinder ourUiBinder = GWT.create(IssueMetaViewUiBinder.class);
}
