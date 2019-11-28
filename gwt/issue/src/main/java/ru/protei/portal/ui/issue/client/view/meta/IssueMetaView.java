package ru.protei.portal.ui.issue.client.view.meta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.ent.*;
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
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.InitiatorModel;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitFormSelector;
import ru.protei.portal.ui.common.client.widget.timefield.TimeLabel;
import ru.protei.portal.ui.common.client.widget.timefield.TimeTextBox;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.issue.client.activity.meta.AbstractIssueMetaActivity;
import ru.protei.portal.ui.issue.client.activity.meta.AbstractIssueMetaView;
import ru.protei.portal.ui.sitefolder.client.view.platform.widget.selector.PlatformFormSelector;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class IssueMetaView extends Composite implements AbstractIssueMetaView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initView();
        ensureDebugIds();
    }

    @Override
    public void setMetaActivity(AbstractIssueMetaActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setCaseMeta(CaseObjectMeta caseObjectMeta) {
        caseMeta = caseObjectMeta;

        state.setValue(En_CaseState.getById(caseMeta.getStateId()));
        importance.setValue(En_ImportanceLevel.getById(caseMeta.getImpLevel()));

        product.setValue(ProductShortView.fromProduct(caseMeta.getProduct()));

        PersonShortView managerValue = PersonShortView.fromPerson(caseMeta.getManager());
        if (managerValue != null) managerValue.setName(transliteration(managerValue.getName()));
        manager.setValue(managerValue);

        EntityOption companyValue = EntityOption.fromCompany(caseMeta.getInitiatorCompany());
        if (companyValue != null) companyValue.setDisplayText(transliteration(companyValue.getDisplayText()));
        company.setValue(companyValue);

        PersonShortView initiatorValue = caseMeta.getInitiator() == null ? null : caseMeta.getInitiator().toFullNameShortView();
        if (initiatorValue != null) initiatorValue.setName(transliteration(initiatorValue.getName()));
        initiator.setValue(initiatorValue);

        platform.setValue(caseMeta.getPlatformId() == null ? null : new PlatformOption(caseMeta.getPlatformName(), caseMeta.getPlatformId()));

        Long timeElapsedValue = caseMeta.getTimeElapsed();
        timeElapsed.setTime(Objects.equals(0L, timeElapsedValue) ? null : timeElapsedValue);
        timeElapsedInput.setTime(timeElapsedValue);
    }

    @Override
    public CaseObjectMeta getCaseMeta() {
        caseMeta.setStateId(state.getValue().getId());
        caseMeta.setImpLevel(importance.getValue().getId());
        caseMeta.setProduct(DevUnit.fromProductShortView(product.getValue()));
        caseMeta.setManager(Person.fromPersonShortView(manager.getValue()));
        caseMeta.setInitiatorCompany(Company.fromEntityOption(company.getValue()));
        caseMeta.setInitiator(Person.fromPersonShortView(initiator.getValue()));
        caseMeta.setPlatformId(platform.getValue() == null ? null : platform.getValue().getId());
        caseMeta.setTimeElapsed(timeElapsedInput.getTime());
        // En_TimeElapsedType elapsedType = timeElapsedType.getValue() != null ? timeElapsedType.getValue() : En_TimeElapsedType.NONE;
        return caseMeta;
    }

    @Override
    public void setCaseMetaNotifiers(CaseObjectMetaNotifiers caseObjectMetaNotifiers) {
        caseMetaNotifiers = caseObjectMetaNotifiers;

        notifiers.setValue(transliterateNotifiers(caseMetaNotifiers.getNotifiers()));
    }

    @Override
    public CaseObjectMetaNotifiers getCaseMetaNotifiers() {
        caseMetaNotifiers.setNotifiers(notifiers.getValue().stream().map(Person::fromPersonShortView).collect(Collectors.toSet()));
        return caseMetaNotifiers;
    }

    @Override
    public void setCaseMetaJira(CaseObjectMetaJira caseObjectMetaJira) {
        caseMetaJira = caseObjectMetaJira;
        if (caseObjectMetaJira == null) return;
        jiraSlaSelector.setValue(caseMetaJira);
    }

    @Override
    public CaseObjectMetaJira getCaseMetaJira() {
        if (caseMetaJira == null) return null;
        caseMetaJira.setSlaMapId(jiraSlaSelector.getValue().getSlaMapId());
        caseMetaJira.setSeverity(jiraSlaSelector.getValue().getSeverity());
        caseMetaJira.setIssueType(jiraSlaSelector.getValue().getIssueType());
        return caseMetaJira;
    }

    @Override
    public void setStateWorkflow(En_CaseStateWorkflow workflow) {
        state.setWorkflow(workflow);
    }

    @Override
    public void applyCompanyValueIfOneOption() {
        company.applyValueIfOneOption();
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
    public void setTimeElapsed(Long timeElapsed) {
        CaseObjectMeta caseMeta = getCaseMeta();
        caseMeta.setTimeElapsed(timeElapsed);
        setCaseMeta(caseMeta);
    }

    @Override
    public void setInitiator(Person initiator) {
        CaseObjectMeta caseMeta = getCaseMeta();
        caseMeta.setInitiator(initiator);
        setCaseMeta(caseMeta);
    }

    @Override
    public void setPlatform(Platform platform) {
        CaseObjectMeta caseMeta = getCaseMeta();
        caseMeta.setPlatformId(platform == null ? null : platform.getId());
        setCaseMeta(caseMeta);
    }

    @Override
    public Element timeElapsedHeader() {
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
    public HasEnabled platformEnabled() {
        return platform;
    }

    @Override
    public HasEnabled initiatorEnabled() {
        return initiator;
    }

    @Override
    public HasVisibility caseSubscriptionContainer() {
        return caseSubscriptionContainers;
    }

    @Override
    public HasVisibility timeElapsedLabelVisibility() {
        return timeElapsed;
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

    private void triggerCaseMeta() {
        if (activity == null) return;
        CaseObjectMeta caseMeta = getCaseMeta();
        activity.onCaseMetaChanged(caseMeta);
    }

    private void triggerCaseMetaNotification() {
        if (activity == null) return;
        CaseObjectMetaNotifiers caseMetaNotifiers = getCaseMetaNotifiers();
        activity.onCaseMetaNotifiersChanged(caseMetaNotifiers);
    }

    private void triggerCaseMetaJira() {
        if (activity == null) return;
        CaseObjectMetaJira caseMetaJira = getCaseMetaJira();
        activity.onCaseMetaJiraChanged(caseMetaJira);
    }

    private void initView() {
        importance.setDefaultValue(lang.selectIssueImportance());
        platform.setDefaultValue(lang.selectPlatform());
        company.setDefaultValue(lang.selectIssueCompany());
        company.showDeprecated(false);
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

    private Set<PersonShortView> transliterateNotifiers(Set<Person> notifiers) {
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
        triggerCaseMeta();
    }

    @UiHandler("importance")
    public void onImportanceChanged(ValueChangeEvent<En_ImportanceLevel> event) {
        triggerCaseMeta();
    }

    @UiHandler("product")
    public void onProductChanged(ValueChangeEvent<ProductShortView> event) {
        triggerCaseMeta();
    }

    @UiHandler("manager")
    public void onManagerChanged(ValueChangeEvent<PersonShortView> event) {
        triggerCaseMeta();
    }

    @UiHandler("company")
    public void onCompanyChanged(ValueChangeEvent<EntityOption> event) {
        if (activity != null) activity.onCompanyChanged();
        triggerCaseMeta();
    }

    @UiHandler("initiator")
    public void onAddContactEvent(AddEvent event) {
        if (activity == null) return;
        activity.onCreateContactClicked();
    }

    @UiHandler("initiator")
    public void onInitiatorChanged(ValueChangeEvent<PersonShortView> event) {
        triggerCaseMeta();
    }

    @UiHandler("platform")
    public void onPlatformChanged(ValueChangeEvent<PlatformOption> event) {
        triggerCaseMeta();
    }

    @UiHandler("timeElapsedInput")
    public void onTimeElapsedChanged(ValueChangeEvent<String> event) {
        triggerCaseMeta();
    }

    @UiHandler("timeElapsedType")
    public void onTimeElapsedTypeChanged(ValueChangeEvent<En_TimeElapsedType> event) {
        triggerCaseMeta();
    }

    @UiHandler("notifiers")
    public void onNotifiersChanged(ValueChangeEvent<Set<PersonShortView>> event) {
        triggerCaseMetaNotification();
    }

    @UiHandler("jiraSlaSelector")
    public void onJiraSlaChanged(ValueChangeEvent<CaseObjectMetaJira> event) {
        triggerCaseMetaJira();
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
    @UiField
    HTMLPanel platformContainer;
    @Inject
    @UiField(provided = true)
    JiraSLASelector jiraSlaSelector;
    @UiField
    HTMLPanel timeElapsedContainer;
    @UiField
    DivElement timeElapsedHeader;
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
    HTMLPanel caseSubscriptionContainers;
    @UiField
    LabelElement newIssueTimeElapsedLabel;
    @UiField
    LabelElement notifiersLabel;
    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector notifiers;

    private AbstractIssueMetaActivity activity;
    private CaseObjectMeta caseMeta;
    private CaseObjectMetaNotifiers caseMetaNotifiers;
    private CaseObjectMetaJira caseMetaJira;

    interface IssueMetaViewUiBinder extends UiBinder<HTMLPanel, IssueMetaView> {}
    private static IssueMetaViewUiBinder ourUiBinder = GWT.create(IssueMetaViewUiBinder.class);
}