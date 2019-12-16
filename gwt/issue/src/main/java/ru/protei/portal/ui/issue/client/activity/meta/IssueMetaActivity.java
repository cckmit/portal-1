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
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.common.shared.model.ShortRequestCallback;
import ru.protei.portal.ui.issue.client.common.CaseStateFilterProvider;

import java.util.*;
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
    public void onAuthSuccess(AuthEvents.Success event) {
        this.authProfile = event.profile;
    }

    @Event
    public void onShow( IssueEvents.EditMeta event ) {
        event.parent.clear();
        event.parent.add(metaView.asWidget());

        this.meta = event.meta;
        caseMetaJira = event.metaJira;
        metaNotifiers = event.metaNotifiers;

        fillView( event.meta );
        fillNotifiersView( event.metaNotifiers );
        fillJiraView( event.metaJira );
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
        meta.setStateId(metaView.state().getValue().getId());
        onCaseMetaChanged( meta );
    }

    @Override
    public void onImportanceChanged() {
        meta.setImpLevel(metaView.importance().getValue().getId());
        onCaseMetaChanged( meta );
    }

    @Override
    public void onProductChanged() {
        meta.setProduct(metaView.getProduct());
        onCaseMetaChanged( meta );
    }

    @Override
    public void onManagerChanged() {
        meta.setManager(metaView.getManager());
        onCaseMetaChanged( meta );
    }

    @Override
    public void onInitiatorChanged() {
        meta.setInitiator(metaView.getInitiator());
        onCaseMetaChanged( meta );
    }

    @Override
    public void onPlatformChanged() {
        meta.setPlatformId(metaView.getPlatformId());
        onCaseMetaChanged( meta );
    }

    @Override
    public void onTimeElapsedChanged() {
        meta.setTimeElapsed(metaView.getTimeElapsed());
        onCaseMetaChanged( meta );
    }

    private void onCaseMetaChanged(CaseObjectMeta caseMeta) {
        if (!validateCaseMeta(caseMeta)) {
            return;
        }

        issueService.updateIssueMeta(caseMeta, new FluentCallback<CaseObjectMeta>()
                .withSuccess(caseMetaUpdated -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
//                    Company companyBeforeUpdate = issue.getInitiatorCompany();
//                    issue = caseMetaUpdated.collectToCaseObject(issue);
//                    Company companyAfterUpdate = issue.getInitiatorCompany();
                    fillView( caseMetaUpdated );//          metaView.setCaseMeta(caseMetaUpdated);
//                    showComments(issue);

//                    if (!Objects.equals(companyBeforeUpdate, companyAfterUpdate)) {
//                        onCompanyChanged();
//                    }
                }));
    }


    @Override
    public void onCaseMetaNotifiersChanged() {
        Set<Person> caseMetaNotifiers = metaView.getCaseMetaNotifiers();

        metaNotifiers.setNotifiers( caseMetaNotifiers );

        issueService.updateIssueMetaNotifiers(metaNotifiers, new FluentCallback<CaseObjectMetaNotifiers>()
                .withSuccess(caseMetaNotifiersUpdated -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
//                    issue = caseMetaNotifiersUpdated.collectToCaseObject(issue);
                    fillNotifiersView( caseMetaNotifiersUpdated ); //  metaView.setCaseMetaNotifiers(caseMetaNotifiersUpdated.getNotifiers());
//                    showComments(issue);
                }));
    }

    @Override
    public void onCaseMetaJiraChanged() {
        CaseObjectMetaJira metaJira = metaView.jiraSlaSelector().getValue();
        caseMetaJira.setSlaMapId(metaJira.getSlaMapId());
        caseMetaJira.setSeverity(metaJira.getSeverity());
        caseMetaJira.setIssueType(metaJira.getIssueType());

        issueService.updateIssueMetaJira(caseMetaJira, new FluentCallback<CaseObjectMetaJira>()
                .withSuccess(caseMetaJiraUpdated -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
//                    issue = caseMetaJiraUpdated.collectToCaseObject(issue);
                    caseMetaJira = caseMetaJiraUpdated;
                    fillJiraView( caseMetaJiraUpdated ); // metaView.setCaseMetaJira(caseMetaJiraUpdated);
//                    showComments(issue);
                }));
    }


    @Override
    public void onCompanyChanged() {
        Company company = metaView.getCompany();

        metaView.initiatorUpdateCompany(company);

        Long selectedCompanyId = company.getId();

        metaView.setPlatform(null);
        metaView.setPlatformFilter(platformOption -> selectedCompanyId.equals(platformOption.getCompanyId()));

        companyService.getCompanyWithParentCompanySubscriptions(
                selectedCompanyId,
                new ShortRequestCallback<List<CompanySubscription>>()
                        .setOnSuccess(subscriptions -> setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(
                                subscriptions,
                                CollectionUtils.isEmpty(subscriptions) ?
                                        lang.issueCompanySubscriptionNotDefined() :
                                        lang.issueCompanySubscriptionBasedOnPrivacyNotDefined()
                                )
                        ))
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
        if (Objects.equals( meta.getInitiator().getCompanyId(), selectedCompanyId)) {
            initiator = meta.getInitiator();
        } else if (Objects.equals(profile.getCompany().getId(), selectedCompanyId)) {
            initiator = Person.fromPersonShortView(new PersonShortView(transliteration(profile.getFullName()), profile.getId(), profile.isFired()));
        }

        metaView.setInitiator(initiator);

        fireEvent(new CaseStateEvents.UpdateSelectorOptions());
    }

    @Override
    public void onCreateContactClicked() {
        if (metaView.getCompany() != null) {
//            fillIssueObject(issue);
            fireEvent(new ContactEvents.Edit(null, metaView.getCompany(), CrmConstants.Issue.CREATE_CONTACT_IDENTITY));
        }
    }

    private void requestIssue(Long number) {
        issueService.getIssueMeta(number, new RequestCallback<CaseObjectMeta>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(CaseObjectMeta meta) {
                IssueMetaActivity.this.meta = meta;

                fillView(meta);
            }
        });
    }

    private void fillNotifiersView(CaseObjectMetaNotifiers caseMetaNotifiers) {

        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW)) { //TODO change rule
        } else {
            caseMetaNotifiers.setNotifiers(null);
        }

        metaView.setCaseMetaNotifiers(caseMetaNotifiers.getNotifiers());
    }

    private void fillJiraView(CaseObjectMetaJira caseMetaJira) {
        metaView.jiraSlaSelectorVisibility().setVisible( caseMetaJira != null );
//        metaView.jiraSlaSelectorVisibility().setVisible( true);
        metaView.setCaseMetaJira( caseMetaJira );
    }

    private void fillView(CaseObjectMeta meta) {

//        CaseObjectMeta caseMeta = new CaseObjectMeta(issue);
//        CaseObjectMetaNotifiers caseMetaNotifiers = new CaseObjectMetaNotifiers(issue);//TODO fill
//        CaseObjectMetaNotifiers caseMetaNotifiers = new CaseObjectMetaNotifiers(issue);//TODO fill
//        CaseObjectMetaJira caseMetaJira = new CaseObjectMetaJira(issue);//TODO fill

        metaView.companyEnabled().setEnabled( isCompanyChangeAllowed(meta.isPrivateCase()) );
        metaView.productEnabled().setEnabled( policyService.hasPrivilegeFor( En_Privilege.ISSUE_PRODUCT_EDIT ) );
        metaView.managerEnabled().setEnabled( policyService.hasPrivilegeFor( En_Privilege.ISSUE_MANAGER_EDIT) );

        metaView.timeElapsedHeaderVisibility().setVisible(true);

        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW)) { //TODO change rule
            metaView.caseSubscriptionContainer().setVisible(true);
        } else {
//            caseMetaNotifiers.setNotifiers(null);
            metaView.caseSubscriptionContainer().setVisible(false);
        }

        metaView.importance().setValue( meta.getImportance() );//        caseMeta.setImportance(caseMeta.getImportance());
        metaView.setStateWorkflow(recognizeWorkflow(meta.getExtAppType()));//Обязательно сетить до установки значения!
        metaView.state().setValue( meta.getState() ); //        caseMeta.setState(caseMeta.getState());
        metaView.stateEnabled().setEnabled(true);

        metaView.timeElapsedContainerVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW));
        metaView.timeElapsedEditContainerVisibility().setVisible(false);
        metaView.setTimeElapsed(meta.getTimeElapsed());

        metaView.setCompany(meta.getInitiatorCompany());//caseMeta.setInitiatorCompany(issue.getInitiatorCompany());
        metaView.setInitiator(meta.getInitiator());//      caseMeta.setInitiator(issue.getInitiator());
        metaView.initiatorUpdateCompany(meta.getInitiatorCompany());
        metaView.setPlatformFilter(platformOption -> meta.getInitiatorCompanyId().equals(platformOption.getCompanyId()));

        metaView.platformVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PLATFORM_EDIT));

//        if (En_ExtAppType.JIRA.getCode().equals(issue.getExtAppType())) {
//            metaView.jiraSlaSelectorVisibility().setVisible(true);
//        } else {
//            metaView.jiraSlaSelectorVisibility().setVisible(false);
//            caseMetaJira = null;
//        }

        companyService.getCompanyWithParentCompanySubscriptions(
                meta.getInitiatorCompanyId(),
                new ShortRequestCallback<List<CompanySubscription>>()
                        .setOnSuccess(subscriptions -> setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(
                                subscriptions,
                                CollectionUtils.isEmpty(subscriptions) ?
                                        lang.issueCompanySubscriptionNotDefined() :
                                        lang.issueCompanySubscriptionBasedOnPrivacyNotDefined()
                                )
                        ))
        );

        companyService.getCompanyCaseStates(
                meta.getInitiatorCompanyId(),
                new ShortRequestCallback<List<CaseState>>()
                        .setOnSuccess(caseStates -> {
                            metaView.setStateFilter(caseStateFilter.makeFilter(caseStates));
                            fireEvent(new CaseStateEvents.UpdateSelectorOptions());
                        })
        );

        fireEvent(new CaseStateEvents.UpdateSelectorOptions());

        metaView.setProduct( meta.getProduct() );
        metaView.setManager( meta.getManager() );
//        metaView.setCaseMeta(caseMeta);
//        metaView.setCaseMetaNotifiers(caseMetaNotifiers);
//        metaView.setCaseMetaJira(caseMetaJira);
    }

//    private void fillIssueObject(CaseObject issue) {
//        boolean isAllowedEditNameAndDescription = isSelfIssue(issue);
//        if (isAllowedEditNameAndDescription) {
//            issue.setName(view.name().getValue());
//            issue.setInfo(view.description().getValue());
//        }
//
//        if (metaView.getCaseMeta() != null) metaView.getCaseMeta().collectToCaseObject(issue);
//        if (metaView.getCaseMetaNotifiers() != null) metaView.getCaseMetaNotifiers().collectToCaseObject(issue);
//        if (metaView.getCaseMetaJira() != null) metaView.getCaseMetaJira().collectToCaseObject(issue);
//    }
//
//    private void showComments(CaseObject issue) {
//        fireEvent(new CaseCommentEvents.Show(view.getCommentsContainer())
//                .withCaseType(En_CaseType.CRM_SUPPORT)
//                .withCaseId(issue.getId())
//                .withModifyEnabled(policyService.hasEveryPrivilegeOf(En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT))
//                .withElapsedTimeEnabled(policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW))
//                .withPrivateVisible(!issue.isPrivateCase() && policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW))
//                .withPrivateCase(issue.isPrivateCase())
//                .withTextMarkup(CaseTextMarkupUtil.recognizeTextMarkup(issue)));
//    }

    private boolean validateCaseMeta(CaseObjectMeta caseMeta) {

        if (caseMeta.getInitiatorCompany() == null) {
            fireEvent(new NotifyEvents.Show(lang.errSaveIssueNeedSelectCompany(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (caseMeta.getManager() == null && isStateWithRestrictions(caseMeta.getState())) {
            fireEvent(new NotifyEvents.Show(lang.errSaveIssueNeedSelectManager(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (caseMeta.getProduct() == null && isStateWithRestrictions(caseMeta.getState())) {
            fireEvent(new NotifyEvents.Show(lang.errProductNotSelected(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        boolean isFieldsValid =
                metaView.stateValidator().isValid() &&
                metaView.importanceValidator().isValid() &&
                metaView.companyValidator().isValid();

        if (!isFieldsValid) {
            fireEvent(new NotifyEvents.Show(lang.errSaveIssueFieldsInvalid(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        return true;
    }

//    private boolean isSelfIssue(CaseObject issue) {
//        return issue.getCreator() != null && Objects.equals(issue.getCreator().getId(), authProfile.getId());
//    }

    private String getSubscriptionsBasedOnPrivacy(List<CompanySubscription> subscriptionsList, String emptyMessage) {
        this.subscriptionsList = subscriptionsList;
        this.subscriptionsListEmptyMessage = emptyMessage;

        if (CollectionUtils.isEmpty(subscriptionsList)) return subscriptionsListEmptyMessage;

        List<String> subscriptionsBasedOnPrivacyList = subscriptionsList.stream()
                .map(CompanySubscription::getEmail)
                .filter(mail -> !meta.isPrivateCase() || CompanySubscription.isProteiRecipient(mail)).collect( Collectors.toList());

        return CollectionUtils.isEmpty(subscriptionsBasedOnPrivacyList)
                ? subscriptionsListEmptyMessage
                : String.join(", ", subscriptionsBasedOnPrivacyList);
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
        metaView.companyEnabled().setEnabled(isCompanyChangeAllowed( meta.isPrivateCase()));
    }

    private boolean isStateWithRestrictions(En_CaseState caseState) {
        return !En_CaseState.CREATED.equals(caseState) &&
                !En_CaseState.CANCELED.equals(caseState);
    }

    private String transliteration(String input) {
        return TransliterationUtils.transliterate(input, LocaleInfo.getCurrentLocale().getLocaleName());
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

    @ContextAware
    CaseObjectMeta meta;
    @ContextAware
    CaseObjectMetaNotifiers metaNotifiers;
    @ContextAware
    CaseObjectMetaJira caseMetaJira;

    private List<CompanySubscription> subscriptionsList;
    private String subscriptionsListEmptyMessage;
    private Profile authProfile;



    private static final Logger log = Logger.getLogger( IssueMetaActivity.class.getName());

}
