package ru.protei.portal.ui.issue.client.view.meta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.view.selector.ElapsedTimeTypeFormSelector;
import ru.protei.portal.ui.common.client.widget.issueimportance.ImportanceFormSelector;
import ru.protei.portal.ui.common.client.widget.issuestate.IssueStateFormSelector;
import ru.protei.portal.ui.common.client.widget.jirasla.JiraSLASelector;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyModel;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.InitiatorModel;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitFormSelector;
import ru.protei.portal.ui.common.client.widget.timefield.HasTime;
import ru.protei.portal.ui.common.client.widget.timefield.TimeLabel;
import ru.protei.portal.ui.common.client.widget.timefield.TimeTextBox;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.issue.client.activity.meta.AbstractIssueMetaActivity;
import ru.protei.portal.ui.issue.client.activity.meta.AbstractIssueMetaView;
import ru.protei.portal.ui.common.client.widget.selector.platform.PlatformFormSelector;

import java.util.*;
import java.util.stream.Collectors;

public class IssueMetaView extends Composite implements AbstractIssueMetaView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        company.setAsyncModel( companyModel );
        initView();

        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractIssueMetaActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<En_CaseState> state( ) {
        return state;
    }

    @Override
    public  HasValue<En_ImportanceLevel> importance( ) {
        return importance;
    }

    @Override
    public void fillImportanceOptions(List<En_ImportanceLevel> options) {
        importance.fillOptions(options);
    }

    @Override
    public void setProduct(DevUnit product) {
        this.product.setValue(ProductShortView.fromProduct(product));
    }

    @Override
    public DevUnit getProduct() {
        return DevUnit.fromProductShortView(product.getValue());
    }

    @Override
    public void setManager( Person manager ) {
        PersonShortView managerValue = PersonShortView.fromPerson(manager);
        if (managerValue != null) managerValue.setName(transliteration(managerValue.getName()));
        this.manager.setValue(managerValue);
    }

    @Override
    public Person getManager() {
        return Person.fromPersonShortView( manager.getValue() );
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
    public void setStateWorkflow(En_CaseStateWorkflow workflow) {
        state.setWorkflow(workflow);
    }

    @Override
    public void setSubscriptionEmails(String value) {
        subscriptions.setInnerText(value);
    }

    @Override
    public void initiatorSelectorAllowAddNew(boolean isVisible) {
        initiator.setAddButtonVisible(isVisible);
    }

    @Override
    public void initiatorUpdateCompany(Company company) {
        initiator.updateCompanies(InitiatorModel.makeCompanyIds(company));
    }

    @Override
    public void setStateFilter(Selector.SelectorFilter<En_CaseState> filter) {
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
    public void setProductTypes(En_DevUnitType... enDevUnitTypes) {
        product.setTypes(enDevUnitTypes);
    }

    @Override
    public void setInitiator(Person initiator) {
        PersonShortView initiatorValue = initiator == null ? null : initiator.toFullNameShortView();
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

    private void initView() {
        importance.setDefaultValue(lang.selectIssueImportance());
        platform.setDefaultValue(lang.selectPlatform());
        company.setDefaultValue(lang.selectIssueCompany());
        companyModel.showDeprecated(false);
        product.setDefaultValue(lang.selectIssueProduct());
        manager.setDefaultValue(lang.selectIssueManager());
        initiator.setDefaultValue(lang.selectIssueInitiator());
        initiator.setAddButtonText(lang.personCreateNew());
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) return;
        state.setEnsureDebugId(DebugIds.ISSUE.STATE_SELECTOR);
        importance.setEnsureDebugId(DebugIds.ISSUE.IMPORTANCE_SELECTOR);
        platform.setEnsureDebugId(DebugIds.ISSUE.PLATFORM_SELECTOR);
        company.setEnsureDebugId(DebugIds.ISSUE.COMPANY_SELECTOR);
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
        initiator.ensureLabelDebugId(DebugIds.ISSUE.LABEL.CONTACT);
        product.ensureLabelDebugId(DebugIds.ISSUE.LABEL.PRODUCT);
        manager.ensureLabelDebugId(DebugIds.ISSUE.LABEL.MANAGER);
        timeElapsedLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.TIME_ELAPSED);
        newIssueTimeElapsedLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.NEW_ISSUE_TIME_ELAPSED);
        subscriptionsLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.SUBSCRIPTIONS);
        notifiersLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.NOTIFIERS);
        timeElapsedType.ensureLabelDebugId(DebugIds.ISSUE.LABEL.TIME_ELAPSED_TYPE);
        notifiers.ensureDebugId(DebugIds.ISSUE.NOTIFIERS_SELECTOR);
    }

    private String transliteration(String input) {
        return TransliterationUtils.transliterate(input, LocaleInfo.getCurrentLocale().getLocaleName());
    }

    private Set<PersonShortView> transliterateNotifiers(Collection<Person> notifiers) {
        return notifiers == null ? new HashSet<>() :
                notifiers
                        .stream()
                        .map(notifier -> {
                            PersonShortView personShortView = PersonShortView.fromPerson(notifier);
                            personShortView.setName(transliteration(personShortView.getName()));
                            return personShortView;
                        })
                        .collect(Collectors.toSet());
    }

    @UiHandler("state")
    public void onStateChanged(ValueChangeEvent<En_CaseState> event) {
        activity.onStateChange();
    }

    @UiHandler("importance")
    public void onImportanceChanged(ValueChangeEvent<En_ImportanceLevel> event) {
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
    public void onCompanyChanged(ValueChangeEvent<EntityOption> event) {
        if (activity != null) activity.onCompanyChanged();
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

    @UiField
    @Inject
    Lang lang;
    @Inject
    @UiField(provided = true)
    IssueStateFormSelector state;
    @Inject
    @UiField(provided = true)
    ImportanceFormSelector importance;
    @Inject
    @UiField(provided = true)
    DevUnitFormSelector product;
    @Inject
    @UiField(provided = true)
    EmployeeFormSelector manager;
    @Inject
    @UiField(provided = true)
    CompanyFormSelector company;
    @Inject
    @UiField(provided = true)
    PersonFormSelector initiator;
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

    @Inject
    CompanyModel companyModel;

    private AbstractIssueMetaActivity activity;

    interface IssueMetaViewUiBinder extends UiBinder<HTMLPanel, IssueMetaView> {}
    private static IssueMetaViewUiBinder ourUiBinder = GWT.create(IssueMetaViewUiBinder.class);
}
