package ru.protei.portal.ui.issue.client.activity.meta;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DefaultSlaValues;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.*;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.ShortRequestCallback;
import ru.protei.portal.ui.issue.client.common.CaseStateFilterProvider;
import ru.protei.portal.ui.issue.client.view.meta.IssueMetaView;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.util.CaseStateWorkflowUtil.recognizeWorkflow;

/**
 *
 */
public abstract class IssueMetaActivity implements AbstractIssueMetaActivity, Activity {

    @PostConstruct
    public void onInit() {
        log.info( "onInit():" );
        metaView.setActivity( this );
    }

    @Event
    public void onShow( IssueEvents.EditMeta event ) {
        event.parent.clear();
        event.parent.add(metaView.asWidget());

        readOnly = event.isReadOnly;
        meta = event.meta;
        caseMetaJira = event.metaJira;
        metaNotifiers = event.metaNotifiers;
        slaList = new ArrayList<>();

        fillView( event.meta );
        fillNotifiersView( event.metaNotifiers );
        fillJiraView( event.metaJira );

        if (!validateCaseMeta(meta)){
            fireEvent(new NotifyEvents.Show(lang.errFieldsRequired(), NotifyEvents.NotifyType.INFO));
        }
    }

    @Event
    public void onChangeTimeElapsed( IssueEvents.ChangeTimeElapsed event ) {
        metaView.setTimeElapsed(event.timeElapsed);
    }

    @Event
    public void onFillPerson(PersonEvents.PersonCreated event) {
        if (CrmConstants.Issue.CREATE_CONTACT_IDENTITY.equals(event.origin) && event.person != null) {
            metaView.setInitiator(event.person);
        }
    }

    @Override
    public void onStateChange() {
        if (metaView.state().getValue().equals(En_CaseState.CREATED) && meta.getManager() != null){
            fireEvent(new NotifyEvents.Show(lang.errSaveIssueNeedUnselectManager(), NotifyEvents.NotifyType.ERROR));
            metaView.state().setValue(meta.getState());
            return;
        }

        meta.setStateId(metaView.state().getValue().getId());
        meta.setPauseDate((!En_CaseState.PAUSED.equals(meta.getState()) || metaView.pauseDate().getValue() == null) ? null : metaView.pauseDate().getValue().getTime());

        metaView.managerCompanyEnabled().setEnabled(policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_EDIT) && En_CaseState.NOT_PROTEI_RESPONSIBILITY.equals(metaView.state().getValue()));
        metaView.pauseDateContainerVisibility().setVisible(En_CaseState.PAUSED.equals(meta.getState()));
        metaView.pauseDate().setValue(!En_CaseState.PAUSED.equals(meta.getState()) ? null : metaView.pauseDate().getValue());

        if (!isPauseDateValid(meta.getState(), meta.getPauseDate())) {
            metaView.setPauseDateValid(false);
            return;
        }

        metaView.setPauseDateValid(true);

        onCaseMetaChanged(meta, () -> fireEvent(new IssueEvents.IssueStateChanged(meta.getId())));
    }

    @Override
    public void onImportanceChanged() {
        meta.setImpLevel(metaView.importance().getValue().getId());
        onCaseMetaChanged(meta, () -> fireEvent(new IssueEvents.IssueImportanceChanged(meta.getId())));

        if (!isJiraIssue()) {
            fillSla(getSlaByImportanceLevel(slaList, meta.getImpLevel()));
        }
    }

    @Override
    public void onProductChanged() {
        meta.setProduct(metaView.getProduct());
        onCaseMetaChanged( meta, () -> fireEvent( new IssueEvents.ChangeIssue( meta.getId() )));
    }

    @Override
    public void onManagerChanged() {
        meta.setManager(metaView.getManager());
        onCaseMetaChanged( meta, () -> fireEvent( new IssueEvents.IssueManagerChanged( meta.getId() ) ) );
    }

    @Override
    public void onInitiatorChanged() {
        meta.setInitiator(metaView.getInitiator());
        onCaseMetaChanged( meta, () -> fireEvent( new IssueEvents.ChangeIssue( meta.getId() ) ) );
    }

    @Override
    public void onPlatformChanged() {
        meta.setPlatform(metaView.platform().getValue());
        onCaseMetaChanged(meta);
        requestSla(meta.getPlatformId(), slaList -> fillSla(getSlaByImportanceLevel(slaList, meta.getImpLevel())));
    }

    @Override
    public void onTimeElapsedChanged() {
        meta.setTimeElapsed(metaView.getTimeElapsed());
        onCaseMetaChanged( meta );
    }

    @Override
    public void onPauseDateChanged() {
        if (!isPauseDateValid(meta.getState(), metaView.pauseDate().getValue() == null ? null : metaView.pauseDate().getValue().getTime())) {
            metaView.setPauseDateValid(false);
            return;
        }

        meta.setPauseDate(metaView.pauseDate().getValue().getTime());
        metaView.setPauseDateValid(true);

        onCaseMetaChanged(meta, () -> fireEvent(new IssueEvents.IssueStateChanged(meta.getId())));
    }

    @Override
    public void onCaseMetaNotifiersChanged() {

        if (readOnly) {
            fireEvent(new NotifyEvents.Show(lang.errPermissionDenied(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        Set<Person> caseMetaNotifiers = metaView.getCaseMetaNotifiers();

        metaNotifiers.setNotifiers( caseMetaNotifiers );

        issueService.updateIssueMetaNotifiers(metaNotifiers, new FluentCallback<CaseObjectMetaNotifiers>()
                .withSuccess(caseMetaNotifiersUpdated -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fillNotifiersView( caseMetaNotifiersUpdated );
                }));
    }

    @Override
    public void onCaseMetaJiraChanged() {

        if (readOnly) {
            fireEvent(new NotifyEvents.Show(lang.errPermissionDenied(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        CaseObjectMetaJira metaJira = metaView.jiraSlaSelector().getValue();
        caseMetaJira.setSlaMapId(metaJira.getSlaMapId());
        caseMetaJira.setSeverity(metaJira.getSeverity());
        caseMetaJira.setIssueType(metaJira.getIssueType());

        issueService.updateIssueMetaJira(caseMetaJira, new FluentCallback<CaseObjectMetaJira>()
                .withSuccess(caseMetaJiraUpdated -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    caseMetaJira = caseMetaJiraUpdated;
                    fillJiraView( caseMetaJiraUpdated );
                }));
    }

    @Override
    public void onCompanyChanged() {
        Company company = metaView.getCompany();
        if (company.getId().equals(meta.getInitiatorCompanyId())) {
            return;
        }

        fillImportanceSelector(company.getId());

        meta.setInitiatorCompany(company);

        metaView.initiatorUpdateCompany(company);
        meta.setInitiator(null);

        Long selectedCompanyId = company.getId();

        metaView.setPlatformFilter(platformOption -> selectedCompanyId.equals(platformOption.getCompanyId()));

        companyService.getCompanyWithParentCompanySubscriptions(
                selectedCompanyId,
                new ShortRequestCallback<List<CompanySubscription>>()
                        .setOnSuccess(subscriptions -> {
                            subscriptions = filterByPlatformAndProduct(subscriptions);
                            setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(
                                    subscriptions,
                                    CollectionUtils.isEmpty(subscriptions) ?
                                            lang.issueCompanySubscriptionNotDefined() :
                                            lang.issueCompanySubscriptionBasedOnPrivacyNotDefined()
                                    )
                            );
                        })
        );

        companyService.getCompanyCaseStates(
                selectedCompanyId,
                new ShortRequestCallback<List<CaseState>>()
                        .setOnSuccess(caseStates -> {
                            metaView.setStateFilter(caseStateFilter.makeFilter(caseStates));
                            fireEvent(new CaseStateEvents.UpdateSelectorOptions());
                        })
        );

        Person initiator = null;
        Profile profile = policyService.getProfile();
        if (meta.getInitiator() != null && Objects.equals( meta.getInitiator().getCompanyId(), selectedCompanyId)) {
            initiator = meta.getInitiator();
        } else if (Objects.equals(profile.getCompany().getId(), selectedCompanyId)) {
            initiator = Person.fromPersonFullNameShortView(new PersonShortView(transliteration(profile.getFullName()), profile.getId(), profile.isFired()));
            meta.setInitiator( initiator );
        }

        metaView.setInitiator(initiator);

        fireEvent(new CaseStateEvents.UpdateSelectorOptions());

        PlatformQuery query = new PlatformQuery();
        query.setCompanyId(selectedCompanyId);

        siteFolderController.getPlatformsOptionList(query, new FluentCallback<List<PlatformOption>>()
                .withError(throwable -> {
                    metaView.platform().setValue(null);
                    meta.setPlatform(null);
                    requestSla(meta.getPlatformId(), slaList -> fillSla(getSlaByImportanceLevel(slaList, meta.getImpLevel())));
                    onCaseMetaChanged(meta, () -> fireEvent(new IssueEvents.ChangeIssue(meta.getId())));
                })
                .withSuccess( result -> {
                    if(result != null && result.size() == 1){
                        metaView.platform().setValue(result.get(0));
                        meta.setPlatform(result.get(0));
                    } else {
                        metaView.platform().setValue(null);
                        meta.setPlatform(null);
                    }
                    requestSla(meta.getPlatformId(), slaList -> fillSla(getSlaByImportanceLevel(slaList, meta.getImpLevel())));
                    onCaseMetaChanged(meta, () -> fireEvent(new IssueEvents.ChangeIssue(meta.getId())));
                } ));
    }

    @Override
    public void onCreateContactClicked() {
        if (metaView.getCompany() != null) {
            fireEvent(new ContactEvents.Edit(null, metaView.getCompany(), CrmConstants.Issue.CREATE_CONTACT_IDENTITY));
        }
    }

    @Override
    public void onManagerCompanyChanged() {
        meta.setManager(null);
        meta.setManagerCompany(metaView.getManagerCompany());

        metaView.setManager(null);
        metaView.managerUpdateCompany(metaView.getManagerCompany().getId());

        metaView.managerValidator().setValid(false);
    }

    private void onCaseMetaChanged(CaseObjectMeta caseMeta) {
        onCaseMetaChanged(caseMeta, null);
    }

    private void onCaseMetaChanged(CaseObjectMeta caseMeta, Runnable runAfterUpdate) {

        if (readOnly) {
            fireEvent(new NotifyEvents.Show(lang.errPermissionDenied(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        if (!validateCaseMeta(caseMeta)) {
            return;
        }

        issueService.updateIssueMeta(caseMeta, new FluentCallback<CaseObjectMeta>()
                .withSuccess(caseMetaUpdated -> {
                    meta.setState(caseMetaUpdated.getState());
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fillView( caseMetaUpdated );
                    if(runAfterUpdate!=null) runAfterUpdate.run();
                }));
    }


    private void fillImportanceSelector(Long id) {
        metaView.fillImportanceOptions(new ArrayList<>());
        companyService.getImportanceLevels(id, new FluentCallback<List<En_ImportanceLevel>>()
                .withSuccess(importanceLevelList -> {
                    metaView.fillImportanceOptions(importanceLevelList);
                    checkImportanceSelectedValue(importanceLevelList);
                }));
    }

    private void checkImportanceSelectedValue(List<En_ImportanceLevel> importanceLevels) {
        if (!importanceLevels.contains(metaView.importance().getValue())){
            metaView.importance().setValue(null);
        }
    }

    private void fillNotifiersView(CaseObjectMetaNotifiers caseMetaNotifiers) {
        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW)) { //TODO change rule
        } else {
            caseMetaNotifiers.setNotifiers(null);
        }

        metaView.setCaseMetaNotifiers(caseMetaNotifiers.getNotifiers());
        metaView.caseMetaNotifiersEnabled().setEnabled(!readOnly);
    }

    private void fillJiraView(CaseObjectMetaJira caseMetaJira) {
        metaView.jiraSlaSelectorVisibility().setVisible( caseMetaJira != null );
        metaView.setCaseMetaJira( caseMetaJira );
        metaView.caseMetaJiraEnabled().setEnabled(!readOnly);
    }

    private void fillView(CaseObjectMeta meta) {
        metaView.stateEnabled().setEnabled(!readOnly);
        metaView.importanceEnabled().setEnabled(!readOnly);
        metaView.productEnabled().setEnabled(!readOnly && policyService.hasPrivilegeFor( En_Privilege.ISSUE_PRODUCT_EDIT ) );
        metaView.companyEnabled().setEnabled(!readOnly && isCompanyChangeAllowed(meta.isPrivateCase()) );
        metaView.initiatorEnabled().setEnabled(!readOnly);
        metaView.platformEnabled().setEnabled(!readOnly);

        metaView.timeElapsedHeaderVisibility().setVisible(true);

        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW)) { //TODO change rule
            metaView.caseSubscriptionContainer().setVisible(true);
        } else {
            metaView.caseSubscriptionContainer().setVisible(false);
        }

        metaView.setProductTypes(En_DevUnitType.PRODUCT);

        metaView.importance().setValue( meta.getImportance() );
        metaView.setStateWorkflow(recognizeWorkflow(meta.getExtAppType()));//Обязательно сетить до установки значения!
        metaView.state().setValue( meta.getState() );
        metaView.pauseDate().setValue(meta.getPauseDate() == null ? null : new Date(meta.getPauseDate()));
        metaView.pauseDateContainerVisibility().setVisible(En_CaseState.PAUSED.equals(meta.getState()));
        metaView.setPauseDateValid(isPauseDateValid(meta.getState(), meta.getPauseDate()));

        metaView.timeElapsedContainerVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW));
        metaView.timeElapsedEditContainerVisibility().setVisible(false);
        metaView.setTimeElapsed(meta.getTimeElapsed());

        metaView.setCompany(meta.getInitiatorCompany());
        metaView.initiatorUpdateCompany(meta.getInitiatorCompany());
        metaView.setInitiator(meta.getInitiator());

        metaView.setPlatformFilter(platformOption -> meta.getInitiatorCompanyId().equals(platformOption.getCompanyId()));
        metaView.platformVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PLATFORM_EDIT));

        companyService.getCompanyWithParentCompanySubscriptions(
                meta.getInitiatorCompanyId(),
                new ShortRequestCallback<List<CompanySubscription>>()
                        .setOnSuccess(subscriptions -> {
                            subscriptions = filterByPlatformAndProduct(subscriptions);
                            setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(
                                    subscriptions,
                                    CollectionUtils.isEmpty(subscriptions) ?
                                            lang.issueCompanySubscriptionNotDefined() :
                                            lang.issueCompanySubscriptionBasedOnPrivacyNotDefined()
                                    )
                            );
                        })
        );

        companyService.getCompanyCaseStates(
                meta.getInitiatorCompanyId(),
                new ShortRequestCallback<List<CaseState>>()
                        .setOnSuccess(caseStates -> {
                            metaView.setStateFilter(caseStateFilter.makeFilter(caseStates));
                            fireEvent(new CaseStateEvents.UpdateSelectorOptions());
                        })
        );
        fillImportanceSelector(meta.getInitiatorCompanyId());

        fireEvent(new CaseStateEvents.UpdateSelectorOptions());

        fillManagerInfoContainer(metaView, meta, readOnly);

        metaView.setProduct( meta.getProduct() );
        metaView.platform().setValue( meta.getPlatformId() == null ? null : new PlatformOption(meta.getPlatformName(), meta.getPlatformId()) );
        metaView.setJiraInfoLink(LinkUtils.makeJiraInfoLink());

        metaView.slaContainerVisibility().setVisible(!isJiraIssue() && isSystemScope());
        requestSla(meta.getPlatformId(), slaList -> fillSla(getSlaByImportanceLevel(slaList, meta.getImpLevel())));
    }

    private void fillManagerInfoContainer(final AbstractIssueMetaView issueMetaView, CaseObjectMeta caseObjectMeta, boolean isReadOnly) {
        issueMetaView.managerEnabled().setEnabled(!isReadOnly && policyService.hasPrivilegeFor(En_Privilege.ISSUE_MANAGER_EDIT));
        issueMetaView.managerCompanyEnabled().setEnabled(policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_EDIT) && En_CaseState.NOT_PROTEI_RESPONSIBILITY.equals(issueMetaView.state().getValue()));

        if (caseObjectMeta.getManagerCompanyId() != null) {
            issueMetaView.setManagerCompany(new EntityOption(caseObjectMeta.getManagerCompanyName(), caseObjectMeta.getManagerCompanyId()));
            issueMetaView.managerUpdateCompany(caseObjectMeta.getManagerCompanyId());
        } else {
            homeCompanyService.getHomeCompany(CrmConstants.Company.HOME_COMPANY_ID, company -> {
                issueMetaView.setManagerCompany(company);
                issueMetaView.managerUpdateCompany(company.getId());
            });
        }

        issueMetaView.setManager(caseObjectMeta.getManager());
    }

    private void requestSla(Long platformId, Consumer<List<ProjectSla>> slaConsumer) {
        if (!isSystemScope()) {
            return;
        }

        if (isJiraIssue()) {
            return;
        }

        if (platformId == null) {
            slaList = DefaultSlaValues.getList();
            metaView.setValuesContainerWarning(true);
            metaView.setSlaTimesContainerTitle(lang.projectSlaDefaultValues());
            slaConsumer.accept(slaList);
            return;
        }

        slaService.getSlaByPlatformId(platformId, new FluentCallback<List<ProjectSla>>()
                .withSuccess(result -> {
                    slaList = result.isEmpty() ? DefaultSlaValues.getList() : result;
                    metaView.setValuesContainerWarning(result.isEmpty());
                    metaView.setSlaTimesContainerTitle(result.isEmpty() ? lang.projectSlaDefaultValues() : lang.projectSlaSetValuesByManager());
                    slaConsumer.accept(slaList);
                })
        );
    }

    private ProjectSla getSlaByImportanceLevel(List<ProjectSla> slaList, final int importanceLevelId) {
        return slaList
                .stream()
                .filter(sla -> Objects.equals(importanceLevelId, sla.getImportanceLevelId()))
                .findAny()
                .orElse(new ProjectSla());
    }

    private void fillSla(ProjectSla sla) {
        metaView.slaReactionTime().setTime(sla.getReactionTime());
        metaView.slaTemporarySolutionTime().setTime(sla.getTemporarySolutionTime());
        metaView.slaFullSolutionTime().setTime(sla.getFullSolutionTime());
    }

    private boolean validateCaseMeta(CaseObjectMeta caseMeta) {

        boolean companyIsValid = caseMeta.getInitiatorCompany() != null;
        metaView.companyValidator().setValid(companyIsValid);

        boolean managerIsValid = caseMeta.getManager() != null || !isStateWithRestrictions(caseMeta.getState());
        metaView.managerValidator().setValid(managerIsValid);

        boolean productIsValid = caseMeta.getProduct() != null || caseMeta.getManager() == null && !isStateWithRestrictions(caseMeta.getState());
        metaView.productValidator().setValid(productIsValid);

        boolean isFieldsValid =
                        productIsValid &&
                        managerIsValid &&
                        companyIsValid;

        return isFieldsValid;
    }

    private String getSubscriptionsBasedOnPrivacy(List<CompanySubscription> subscriptionsList, String emptyMessage) {
        this.subscriptionsListEmptyMessage = emptyMessage;

        if (CollectionUtils.isEmpty(subscriptionsList)) return subscriptionsListEmptyMessage;

        List<String> subscriptionsBasedOnPrivacyList = subscriptionsList.stream()
                .map(CompanySubscription::getEmail)
                .filter(mail -> !meta.isPrivateCase() || CompanySubscription.isProteiRecipient(mail))
                .distinct()
                .collect( Collectors.toList());

        return CollectionUtils.isEmpty(subscriptionsBasedOnPrivacyList)
                ? subscriptionsListEmptyMessage
                : String.join(", ", subscriptionsBasedOnPrivacyList);
    }

    private List<CompanySubscription> filterByPlatformAndProduct(List<CompanySubscription> subscriptionsList) {
        this.subscriptionsList = subscriptionsList;

        if (CollectionUtils.isEmpty(subscriptionsList)) return subscriptionsList;

        return subscriptionsList.stream()
                .filter(companySubscription -> (companySubscription.getProductId() == null || Objects.equals(meta.getProductId(), companySubscription.getProductId()))
                        && (companySubscription.getPlatformId() == null || Objects.equals(meta.getPlatformId(), companySubscription.getPlatformId())))
                .collect( Collectors.toList());
    }

    private boolean isCompanyChangeAllowed(boolean isPrivateCase) {
        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_COMPANY_EDIT) &&
                (subscriptionsList == null || subscriptionsList.isEmpty() || isPrivateCase)
        ) {
            return true;
        }

        return subscriptionsList == null || subscriptionsList.stream()
                .map(CompanySubscription::getEmail)
                .allMatch(CompanySubscription::isProteiRecipient);
    }

    private void setSubscriptionEmails(String value) {
        metaView.setSubscriptionEmails(value);
        metaView.companyEnabled().setEnabled(!readOnly && isCompanyChangeAllowed( meta.isPrivateCase()));
    }

    private boolean isStateWithRestrictions(En_CaseState caseState) {
        return !En_CaseState.CREATED.equals(caseState) &&
                !En_CaseState.CANCELED.equals(caseState);
    }

    private String transliteration(String input) {
        return TransliterationUtils.transliterate(input, LocaleInfo.getCurrentLocale().getLocaleName());
    }

    private boolean isJiraIssue() {
        return caseMetaJira != null;
    }

    private boolean isPauseDateValid(En_CaseState currentState, Long pauseDate) {
        if (!En_CaseState.PAUSED.equals(currentState)) {
            return true;
        }

        if (pauseDate != null && pauseDate > System.currentTimeMillis()) {
            return true;
        }

        return false;
    }

    private boolean isSystemScope() {
        return policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW);
    }

    @Inject
    AbstractIssueMetaView metaView;

    @Inject
    IssueControllerAsync issueService;

    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;
    @Inject
    CompanyControllerAsync companyService;
    @Inject
    CaseStateFilterProvider caseStateFilter;
    @Inject
    SLAControllerAsync slaService;
    @Inject
    SiteFolderControllerAsync siteFolderController;
    @Inject
    HomeCompanyService homeCompanyService;

    @ContextAware
    CaseObjectMeta meta;
    @ContextAware
    CaseObjectMetaNotifiers metaNotifiers;
    @ContextAware
    CaseObjectMetaJira caseMetaJira;

    private List<CompanySubscription> subscriptionsList;
    private String subscriptionsListEmptyMessage;
    private boolean readOnly;
    private List<ProjectSla> slaList;

    private static final Logger log = Logger.getLogger( IssueMetaActivity.class.getName());

}
