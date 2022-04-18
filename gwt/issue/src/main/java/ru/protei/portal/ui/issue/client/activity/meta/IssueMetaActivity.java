package ru.protei.portal.ui.issue.client.activity.meta;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.datepicker.client.CalendarUtil;
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
import ru.protei.portal.core.model.view.*;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.ConfigStorage;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.En_IssueValidationResultLang;
import ru.protei.portal.ui.common.client.lang.En_ResultStatusLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.*;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyModel;
import ru.protei.portal.ui.common.client.widget.selector.company.CustomerCompanyModel;
import ru.protei.portal.ui.common.client.widget.selector.company.SubcontractorCompanyModel;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductWithChildrenModel;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.ShortRequestCallback;
import ru.protei.portal.ui.issue.client.common.CaseStateFilterProvider;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.util.CaseStateWorkflowUtil.recognizeWorkflow;
import static ru.protei.portal.ui.common.client.util.ClientTransliterationUtils.transliteration;

/**
 *
 */
public abstract class IssueMetaActivity implements AbstractIssueMetaActivity, Activity {

    @PostConstruct
    public void onInit() {
        log.info( "onInit():" );
        metaView.setActivity( this );

        productModel.setUnitState(En_DevUnitState.ACTIVE);
        productWithChildrenModel.setUnitState(En_DevUnitState.ACTIVE);

        productModel.setUnitTypes(En_DevUnitType.PRODUCT);
        productWithChildrenModel.setUnitTypes(En_DevUnitType.COMPLEX, En_DevUnitType.PRODUCT);

        companyModel.showDeprecated(false);
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        Company userCompany = event.profile.getCompany();
        customerCompanyModel.setSubcontractorId(userCompany.getId());
        customerCompanyModel.setActive(true);
        subcontractorCompanyModel.setCompanyId(userCompany.getId());
        subcontractorCompanyModel.setActive(true);
        metaView.setCompanyModel(isSubcontractorCompany(userCompany) ? customerCompanyModel : companyModel);
        metaView.setManagerCompanyModel(event.profile.hasSystemScopeForPrivilege(En_Privilege.ISSUE_EDIT) ? subcontractorCompanyModel : companyModel);
    }

    @Event
    public void onShow(IssueEvents.EditMeta event) {
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
        fillPlansView(meta.getPlans());

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

    @Event
    public void onUpdateIssueState(IssueEvents.IssueStateUpdated event) {
        caseStateController.getCaseStateByCaseId(event.issueId, new FluentCallback<CaseState>()
                .withSuccess(caseState -> {
                    if (!Objects.equals(caseState, metaView.state().getValue())) {
                        metaView.state().setValue(caseState);
                        meta.setStateId(caseState.getId());
                        meta.setStateName(caseState.getState());
                        meta.setStateColor(caseState.getColor());
                        fireEvent(new IssueEvents.IssueStateChanged(event.issueId, caseState.getId()));
                    }}));
    }

    @Event
    public void onUpdateIssueNotifiers(IssueEvents.IssueNotifiersUpdated event) {
        issueController.getIssueMetaNotifiers(event.issueId, new FluentCallback<CaseObjectMetaNotifiers>()
                .withSuccess(caseObjectMetaNotifiers -> {
                    metaView.setCaseMetaNotifiers(caseObjectMetaNotifiers.getNotifiers());
                    metaNotifiers.setNotifiers(caseObjectMetaNotifiers.getNotifiers());
                }));

    }

    @Override
    public void onStateChange() {
        CaseState caseState = metaView.state().getValue();
        if (CrmConstants.State.CREATED == caseState.getId() && meta.getManager() != null){
            fireEvent(new NotifyEvents.Show(lang.errSaveIssueNeedUnselectManager(), NotifyEvents.NotifyType.ERROR));
            metaView.state().setValue(new CaseState(meta.getStateId(), meta.getStateName(), meta.getStateColor()));
            return;
        }

        meta.setStateId(caseState.getId());

        metaView.setAutoCloseVisible(!isCustomer() && meta.getStateId() == CrmConstants.State.TEST_CUST && meta.getExtAppType() == null);

        if (meta.getAutoClose() && CrmConstants.State.TEST_CUST != caseState.getId()) {
            meta.setAutoClose(false);
            meta.setDeadline(null);
        }

        meta.setStateName(caseState.getState());
        meta.setStateColor(caseState.getColor());
        meta.setPauseDate((CrmConstants.State.PAUSED != meta.getStateId() || metaView.pauseDate().getValue() == null) ? null : metaView.pauseDate().getValue().getTime());

        metaView.pauseDateContainerVisibility().setVisible(CrmConstants.State.PAUSED == meta.getStateId());
        metaView.pauseDate().setValue(CrmConstants.State.PAUSED != meta.getStateId() ? null : metaView.pauseDate().getValue());

        if (!isPauseDateValid(meta.getStateId(), meta.getPauseDate())) {
            metaView.setPauseDateValid(false);
            return;
        }

        metaView.setPauseDateValid(true);

        onCaseMetaChanged(meta, () -> {
            fireEvent(new IssueEvents.IssueStateChanged(meta.getId(), meta.getStateId()));
            fireEvent(new IssueEvents.IssueMetaChanged(meta));
            onParentIssueChanged(meta.getId());
            fireEvent(new CommentAndHistoryEvents.Reload());
        });
    }

    @Override
    public void onImportanceChanged() {
        meta.setImpLevel(metaView.importance().getValue().getId());
        onCaseMetaChanged(meta, () -> {
            fireEvent(new IssueEvents.IssueImportanceChanged(meta.getId()));
            fireEvent(new IssueEvents.IssueMetaChanged(meta));
            fireEvent(new CommentAndHistoryEvents.Reload());
        });

        if (!isJiraIssue()) {
            fillSla(getSlaByImportanceLevel(slaList, metaView.importance().getValue()));
        }
    }

    @Override
    public void onProductChanged() {
        meta.setProduct(DevUnit.fromProductShortView(metaView.product().getValue()));
        onCaseMetaChanged( meta, () -> {
            fireEvent(new IssueEvents.IssueProductChanged(meta.getId()));
            fireEvent(new IssueEvents.IssueMetaChanged(meta));
            onParentIssueChanged(meta.getId());
            fireEvent(new CommentAndHistoryEvents.Reload());
        });
    }

    @Override
    public void onManagerChanged() {
        meta.setManager(metaView.getManager());
        onCaseMetaChanged(meta, () -> {
            fireEvent(new IssueEvents.IssueManagerChanged(meta.getId()));
            fireEvent(new IssueEvents.IssueMetaChanged(meta));
            onParentIssueChanged(meta.getId());
            fireEvent(new CommentAndHistoryEvents.Reload());
        } );
    }

    @Override
    public void onInitiatorChanged() {
        meta.setInitiator(metaView.getInitiator());
        onCaseMetaChanged( meta, () -> {
            fireEvent(new IssueEvents.ChangeIssue(meta.getId()));
            fireEvent(new IssueEvents.IssueMetaChanged(meta));
            fireEvent(new CommentAndHistoryEvents.Reload());
        } );
    }

    @Override
    public void onPlatformChanged() {
        meta.setPlatform(metaView.platform().getValue());

        Runnable onChanged = () -> {
            fireEvent(new IssueEvents.IssueMetaChanged(meta));
            fireEvent(new CommentAndHistoryEvents.Reload());
        };

        if (isCompanyWithAutoOpenIssues(currentCompany)) {
            resetProduct(meta, metaView);
            updateProductsFilter(metaView, meta.getInitiatorCompanyId(), meta.getPlatformId());

            onChanged = () -> {
                fireEvent(new IssueEvents.ChangeIssue(meta.getId()));
                fireEvent(new IssueEvents.IssueMetaChanged(meta));
                fireEvent(new CommentAndHistoryEvents.Reload());
            };
        }

        onCaseMetaChanged(meta, onChanged);
        requestSla(meta.getPlatformId(), slaList -> fillSla(getSlaByImportanceLevel(slaList, metaView.importance().getValue())));
    }

    @Override
    public void onTimeElapsedChanged() {
        meta.setTimeElapsed(metaView.getTimeElapsed());
        onCaseMetaChanged( meta, () -> {
            fireEvent(new IssueEvents.IssueMetaChanged(meta));
        } );
    }

    @Override
    public void onPauseDateChanged() {
        if (!isPauseDateValid(meta.getStateId(), metaView.pauseDate().getValue() == null ? null : metaView.pauseDate().getValue().getTime())) {
            metaView.setPauseDateValid(false);
            return;
        }

        meta.setPauseDate(metaView.pauseDate().getValue().getTime());
        metaView.setPauseDateValid(true);

        onCaseMetaChanged(meta, () -> {
            fireEvent(new IssueEvents.IssueStateChanged(meta.getId(), meta.getStateId()));
            fireEvent(new IssueEvents.IssueMetaChanged(meta));
        });
    }

    @Override
    public void onCaseMetaNotifiersChanged() {

        if (readOnly) {
            fireEvent(new NotifyEvents.Show(lang.errPermissionDenied(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        Set<Person> caseMetaNotifiers = metaView.getCaseMetaNotifiers();

        metaNotifiers.setNotifiers( caseMetaNotifiers );

        issueController.updateIssueMetaNotifiers(metaNotifiers, new FluentCallback<CaseObjectMetaNotifiers>()
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

        issueController.updateIssueMetaJira(caseMetaJira, new FluentCallback<CaseObjectMetaJira>()
                .withSuccess(caseMetaJiraUpdated -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    caseMetaJira = caseMetaJiraUpdated;
                    fillJiraView( caseMetaJiraUpdated );
                }));
    }

    @Override
    public void onInitiatorCompanyChanged() {
        Company company = metaView.getCompany();
        if (company.getId().equals(meta.getInitiatorCompanyId())) {
            return;
        }

        fillImportanceSelector(company.getId());

        meta.setInitiatorCompany(company);

        metaView.setInitiatorFilter(company.getId());
        meta.setInitiator(null);

        Long selectedCompanyId = company.getId();

        metaView.setPlatformFilter(platformOption -> selectedCompanyId.equals(platformOption.getCompanyId()));

        updateSubscriptions(selectedCompanyId, meta.getManagerCompanyId());

        companyController.getCompanyCaseStates(
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

        companyController.getCompanyOmitPrivileges(selectedCompanyId, new FluentCallback<Company>()
                .withSuccess(resultCompany -> {
                    setCurrentCompany(resultCompany);
                    fillPlatformValueAndUpdateProductsFilter(resultCompany);
                })
        );

        subcontractorCompanyModel.setCompanyId(company.getId());

        fireEvent(new IssueEvents.IssueMetaChanged(meta));
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
        metaView.updateManagersCompanyFilter(metaView.getManagerCompany().getId());

        metaView.managerValidator().setValid(false);

        updateSubscriptions(meta.getInitiatorCompanyId(), meta.getManagerCompanyId());

        onCaseMetaChanged(meta, () -> {
            fireEvent(new IssueEvents.IssueMetaChanged(meta));
            fireEvent(new CommentAndHistoryEvents.Reload());
        });
    }

    @Override
    public void onPlansChanged() {
        if (readOnly) {
            fireEvent(new NotifyEvents.Show(lang.errPermissionDenied(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        issueController.updatePlans(metaView.ownerPlans().getValue(), meta.getId(), new FluentCallback<Set<PlanOption>>()
                .withSuccess(updatedPlans -> {
                    metaView.ownerPlans().setValue(updatedPlans);
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new CommentAndHistoryEvents.Reload());
                })
        );
    }

    @Override
    public void onAutoCloseChanged() {
        meta.setAutoClose(metaView.autoClose().getValue());
        if (metaView.autoClose().getValue()) {
            Date now = new Date();
            CalendarUtil.addDaysToDate(now, configStorage.getConfigData().autoCloseDefaultDeadline);
            meta.setDeadline(now.getTime());
        } else {
            meta.setDeadline(null);
        }

        onCaseMetaChanged(meta, () -> {
            fireEvent(new IssueEvents.ChangeIssue(meta.getId()));
            fireEvent(new IssueEvents.IssueMetaChanged(meta));
            fireEvent(new CommentAndHistoryEvents.Reload());
        });
    }

    @Override
    public void onDeadlineChanged() {
        if (isDeadlineEquals(metaView.deadline().getValue(), meta.getDeadline())) {
            metaView.setDeadlineValid(isDeadlineFieldValid(metaView.isDeadlineEmpty(), metaView.deadline().getValue()));
            return;
        }

        if (!isDeadlineFieldValid(metaView.isDeadlineEmpty(), metaView.deadline().getValue())) {
            metaView.setDeadlineValid(false);
            return;
        }

        meta.setDeadline(metaView.deadline().getValue() != null ? metaView.deadline().getValue().getTime() : null);
        metaView.setDeadlineValid(true);

        onCaseMetaChanged(meta, () -> {
            fireEvent(new IssueEvents.IssueMetaChanged(meta));
            fireEvent(new CommentAndHistoryEvents.Reload());
        });
    }

    @Override
    public void onWorkTriggerChanged() {
        meta.setWorkTrigger(metaView.workTrigger().getValue());
        onCaseMetaChanged( meta, () -> {
            fireEvent(new IssueEvents.IssueMetaChanged(meta));
            fireEvent(new CommentAndHistoryEvents.Reload());
        } );
    }

    private void updateSubscriptions(Long... companyIds) {
        companyController.getCompanyWithParentCompanySubscriptions(
                new HashSet<>(Arrays.asList(companyIds)),
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
    }

    private void onCaseMetaChanged(CaseObjectMeta caseMeta, Runnable runAfterUpdate) {

        if (readOnly) {
            fireEvent(new NotifyEvents.Show(lang.errPermissionDenied(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        if (!validateCaseMeta(caseMeta)) {
            return;
        }

        issueController.updateIssueMeta(caseMeta, new FluentCallback<CaseObjectMeta>()
                .withError(throwable -> {
                    if (throwable instanceof RequestFailedException &&
                            ((RequestFailedException)throwable).status == En_ResultStatus.VALIDATION_ERROR) {
                        RequestFailedException rf = (RequestFailedException)throwable;
                        fireEvent(new NotifyEvents.Show( resultStatusLang.getMessage(rf.status) + ": " +
                                validationResultLang.getMessage(rf.issueValidationResult),
                                NotifyEvents.NotifyType.ERROR));
                    } else {
                        defaultErrorHandler.accept(throwable);
                    }
                })
                .withSuccess(caseMetaUpdated -> {
                    meta.setStateId(caseMetaUpdated.getStateId());
                    meta.setStateName(caseMetaUpdated.getStateName());
                    meta.setStateColor(caseMetaUpdated.getStateColor());
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fillView( caseMetaUpdated );
                    if(runAfterUpdate!=null) runAfterUpdate.run();
                }));
    }


    private void fillImportanceSelector(Long id) {
        importanceService.getImportanceLevels(id, new FluentCallback<List<ImportanceLevel>>()
                .withSuccess(importanceLevelList -> {
                    metaView.fillImportanceOptions(importanceLevelList);
                    checkImportanceSelectedValue(importanceLevelList);
                }));
    }

    private void checkImportanceSelectedValue(List<ImportanceLevel> importanceLevels) {
        if (!importanceLevels.contains(metaView.importance().getValue())) {
            metaView.importance().setValue(null);
            meta.setImpLevel(null);
            meta.setImportanceCode(null);
        }
    }

    private void fillPlansView(List<Plan> plans) {
        if (!policyService.hasPrivilegeFor(En_Privilege.ISSUE_PLAN_EDIT)) {
            metaView.ownerPlansContainerVisibility().setVisible(false);
            metaView.otherPlansContainerVisibility().setVisible(false);
            metaView.setPlansLabelVisible(false);
            return;
        }

        metaView.setPlansLabelVisible(true);
        fillOwnerPlansContainer(metaView, plans, policyService.getProfile());
        fillOtherPlansContainer(metaView, plans, policyService.getProfile());
    }

    private void fillOwnerPlansContainer(final AbstractIssueMetaView issueMetaView, List<Plan> plans, Profile profile) {
        if (!profile.hasPrivilegeFor(En_Privilege.PLAN_EDIT)) {
            issueMetaView.ownerPlansContainerVisibility().setVisible(false);
            return;
        }

        issueMetaView.ownerPlansContainerVisibility().setVisible(true);
        issueMetaView.ownerPlans().setValue(getOwnerPlans(plans, profile.getId()));
        issueMetaView.setPlanCreatorId(profile.getId());
    }

    private void fillOtherPlansContainer(final AbstractIssueMetaView issueMetaView, List<Plan> plans, Profile profile) {
        Set<PlanOption> otherPlans = getOtherPlans(plans, profile.getId());

        issueMetaView.otherPlansContainerVisibility().setVisible(true);
        issueMetaView.setOtherPlans(otherPlans.stream().map(PlanOption::getDisplayText).collect(Collectors.joining(", ")));
    }

    private Set<PlanOption> getOwnerPlans(List<Plan> plans, Long personId) {
        return stream(plans)
                .filter(plan -> personId.equals(plan.getCreatorId()))
                .map(PlanOption::fromPlan)
                .collect(Collectors.toSet());
    }

    private Set<PlanOption> getOtherPlans(List<Plan> plans, Long personId) {
        return stream(plans)
                .filter(plan -> !personId.equals(plan.getCreatorId()))
                .map(PlanOption::fromPlan)
                .collect(Collectors.toSet());
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
        metaView.productEnabled().setEnabled(isProductEnabled(readOnly, meta.getInitiatorCompany()));
        metaView.companyEnabled().setEnabled(!readOnly && isCompanyChangeAllowed(meta.isPrivateCase()));
        metaView.initiatorEnabled().setEnabled(!readOnly && isInitiatorChangeAllowed(meta.getInitiatorCompanyId()));
        metaView.platformEnabled().setEnabled(!readOnly);

        metaView.setAutoCloseVisible(!isCustomer() && meta.getStateId() == CrmConstants.State.TEST_CUST && meta.getExtAppType() == null);

        metaView.timeElapsedHeaderVisibility().setVisible(true);

        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW)) { //TODO change rule
            metaView.caseSubscriptionContainer().setVisible(true);
        } else {
            metaView.caseSubscriptionContainer().setVisible(false);
        }

        importanceService.getImportanceLevel(meta.getImpLevel(), new FluentCallback<ImportanceLevel>()
                .withSuccess(importanceLevel -> {
                    metaView.importance().setValue(importanceLevel);
                    requestSla(meta.getPlatformId(), slaList -> fillSla(getSlaByImportanceLevel(slaList, importanceLevel)));
                })
        );

        metaView.setStateWorkflow(recognizeWorkflow(meta.getExtAppType()));
        metaView.state().setValue(new CaseState(meta.getStateId(), meta.getStateName(), meta.getStateColor(), meta.getStateInfo()));
        metaView.pauseDate().setValue(meta.getPauseDate() == null ? null : new Date(meta.getPauseDate()));
        metaView.pauseDateContainerVisibility().setVisible(CrmConstants.State.PAUSED == meta.getStateId());
        metaView.setPauseDateValid(isPauseDateValid(meta.getStateId(), meta.getPauseDate()));

        metaView.timeElapsedContainerVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW));
        metaView.timeElapsedEditContainerVisibility().setVisible(false);
        metaView.setTimeElapsed(meta.getTimeElapsed());

        setCurrentCompany(meta.getInitiatorCompany());
        subcontractorCompanyModel.setCompanyId(meta.getInitiatorCompanyId());

        metaView.setCompany(meta.getInitiatorCompany());
        metaView.setInitiatorFilter(meta.getInitiatorCompany()==null?null:meta.getInitiatorCompany().getId());
        metaView.setInitiator(meta.getInitiator());

        metaView.setPlatformFilter(platformOption -> meta.getInitiatorCompanyId().equals(platformOption.getCompanyId()));
        setPlatformVisibility(metaView, policyService.hasPrivilegeFor(En_Privilege.ISSUE_PLATFORM_EDIT));

        updateSubscriptions(meta.getInitiatorCompanyId(), meta.getManagerCompanyId());

        companyController.getCompanyCaseStates(
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

        metaView.platform().setValue( meta.getPlatformId() == null ? null : new PlatformOption(meta.getPlatformName(), meta.getPlatformId()) );

        metaView.product().setValue(ProductShortView.fromProduct(meta.getProduct()));
        updateProductModelAndMandatory(metaView, isCompanyWithAutoOpenIssues(meta.getInitiatorCompany()));

        if (isCompanyWithAutoOpenIssues(meta.getInitiatorCompany())) {
            updateProductsFilter(metaView, meta.getInitiatorCompanyId(), meta.getPlatformId());
        }

        metaView.setJiraInfoLink(LinkUtils.makeJiraInfoLink());

        metaView.slaContainerVisibility().setVisible(!isJiraIssue() && isSystemScope());

        metaView.autoClose().setValue(meta.getAutoClose());

        metaView.deadline().setValue(meta.getDeadline() == null ? null : new Date(meta.getDeadline()));
        metaView.setDeadlineValid(isDeadlineValid(meta.getDeadline()));
        metaView.workTrigger().setValue(meta.getWorkTrigger() == null ? En_WorkTrigger.NONE : meta.getWorkTrigger());

        setCustomerVisibility(metaView, policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_CREATE));
    }

    private void fillManagerInfoContainer(final AbstractIssueMetaView issueMetaView, final CaseObjectMeta caseObjectMeta, boolean isReadOnly) {
        if (caseObjectMeta.getManagerCompanyId() != null) {
            issueMetaView.setManagerCompany(new EntityOption(caseObjectMeta.getManagerCompanyName(), caseObjectMeta.getManagerCompanyId()));
            issueMetaView.updateManagersCompanyFilter(caseObjectMeta.getManagerCompanyId());
        } else {
            if (policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_EDIT) || policyService.isSubcontractorCompany()) {
                EntityOption company = policyService.getUserCompany().toEntityOption();
                issueMetaView.setManagerCompany(company);
                issueMetaView.updateManagersCompanyFilter(company.getId());
                caseObjectMeta.setManagerCompany(company);
            } else {
                homeCompanyService.getHomeCompany(CrmConstants.Company.HOME_COMPANY_ID, company -> {
                    issueMetaView.setManagerCompany(company);
                    issueMetaView.updateManagersCompanyFilter(company.getId());
                    caseObjectMeta.setManagerCompany(company);
                });
            }
        }

        issueMetaView.setManager(caseObjectMeta.getManager());

        metaView.managerCompanyEnabled().setEnabled(policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_EDIT));
        issueMetaView.managerEnabled().setEnabled(!isReadOnly && (policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_EDIT) ||
                Objects.equals(issueMetaView.getManagerCompany().getId(), policyService.getProfile().getCompany().getId())));
    }

    private void requestSla(Long platformId, Consumer<List<ProjectSla>> slaConsumer) {
        if (!isSystemScope()) {
            return;
        }

        if (isJiraIssue()) {
            return;
        }

        if (platformId == null) {
            slaList.clear();
            metaView.setValuesContainerWarning(true);
            metaView.setSlaTimesContainerTitle(lang.projectSlaDefaultValues());
            slaConsumer.accept(slaList);
            return;
        }

        slaController.getSlaByPlatformId(platformId, new FluentCallback<List<ProjectSla>>()
                .withSuccess(result -> {
                    if (result.isEmpty()) {
                        slaList.clear();
                    } else {
                        slaList = result;
                    }

                    metaView.setValuesContainerWarning(result.isEmpty());
                    metaView.setSlaTimesContainerTitle(result.isEmpty() ? lang.projectSlaDefaultValues() : lang.projectSlaSetValuesByManager());
                    slaConsumer.accept(slaList);
                })
        );
    }

    private ProjectSla getSlaByImportanceLevel(List<ProjectSla> slaList, final ImportanceLevel importanceLevel) {
        if (slaList.isEmpty()) {
            return makeDefaultProjectSla(importanceLevel);
        }

        return slaList
                .stream()
                .filter(sla -> Objects.equals(importanceLevel.getId(), sla.getImportanceLevelId()))
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

        boolean managerIsValid = caseMeta.getManager() != null || !isStateWithRestrictions(caseMeta.getStateId());
        metaView.managerValidator().setValid(managerIsValid);

        boolean productIsValid = isProductValid(caseMeta);
        metaView.productValidator().setValid(productIsValid);

        boolean importanceLevelIsValid = caseMeta.getImpLevel() != null;
        metaView.importanceValidator().setValid(importanceLevelIsValid);

        boolean isFieldsValid =
                        productIsValid &&
                        managerIsValid &&
                        importanceLevelIsValid &&
                        companyIsValid;

        return isFieldsValid;
    }

    private boolean isProductValid(CaseObjectMeta caseMeta) {
        if (caseMeta.getProduct() != null) {
            return true;
        }

        if (caseMeta.getManager() != null) {
            return false;
        }

        if (isStateWithRestrictions(caseMeta.getStateId())) {
            return false;
        }

        if (isCompanyWithAutoOpenIssues(currentCompany)) {
            return false;
        }

        return true;
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
        if (!policyService.hasPrivilegeFor(En_Privilege.ISSUE_COMPANY_EDIT)) {
            return false;
        }

        if (subscriptionsList == null || subscriptionsList.isEmpty() || isPrivateCase) {
            return true;
        }

        return subscriptionsList.stream()
                .map(CompanySubscription::getEmail)
                .allMatch(CompanySubscription::isProteiRecipient);
    }

    private boolean isInitiatorChangeAllowed(Long initiatorCompanyId) {
        if (policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_EDIT)) {
            return true;
        }

        if (Objects.equals(initiatorCompanyId, policyService.getUserCompany().getId())) {
            return true;
        }

        return false;
    }

    private void setSubscriptionEmails(String value) {
        metaView.setSubscriptionEmails(value);
        metaView.companyEnabled().setEnabled(!readOnly && isCompanyChangeAllowed(meta.isPrivateCase()));
    }

    private boolean isStateWithRestrictions(long caseStateId) {
        return CrmConstants.State.CREATED != caseStateId &&
                CrmConstants.State.CANCELED != caseStateId;
    }

    private boolean isJiraIssue() {
        return caseMetaJira != null;
    }

    private boolean isPauseDateValid(long currentStateId, Long pauseDate) {
        if (CrmConstants.State.PAUSED != currentStateId) {
            return true;
        }

        if (pauseDate != null && pauseDate > System.currentTimeMillis()) {
            return true;
        }

        return false;
    }

    private boolean isDeadlineEquals(Date deadlineField, Long deadlineMeta) {
        if (deadlineField == null) {
            return deadlineMeta == null;
        } else {
            return Objects.equals(deadlineField.getTime(), deadlineMeta);
        }
    }

    private boolean isDeadlineFieldValid(boolean isEmptyDeadlineField, Date date) {
        if (meta.getAutoClose() && date == null) {
            return false;
        }

        if (date == null) {
            return isEmptyDeadlineField;
        }

        return isDeadlineValid(date.getTime());
    }

    private boolean isDeadlineValid(Long date) {
        return date == null || date > System.currentTimeMillis();
    }

    private boolean isSystemScope() {
        return policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW);
    }

    private void setPlatformVisibility(AbstractIssueMetaView issueMetaView, boolean isVisible) {
        issueMetaView.platformVisibility().setVisible(isVisible);
        issueMetaView.setInitiatorBorderBottomVisible(!isVisible);
    }

    private void setCustomerVisibility(AbstractIssueMetaView issueMetaView, boolean isVisible) {
        issueMetaView.deadlineContainerVisibility().setVisible(isVisible);
        issueMetaView.workTriggerVisibility().setVisible(isVisible);
        issueMetaView.setProductBorderBottomVisible(!isVisible);
    }

    private void fillPlatformValueAndUpdateProductsFilter(final Company company) {
        requestPlatforms(company.getId(), platformOptions -> {
            if (platformOptions != null && platformOptions.size() == 1) {
                metaView.platform().setValue(platformOptions.get(0));
                meta.setPlatform(platformOptions.get(0));
            } else {
                metaView.platform().setValue(null);
                meta.setPlatform(null);
            }

            updateProductModelAndMandatory(metaView, isCompanyWithAutoOpenIssues(company));

            if (!isCompanyWithAutoOpenIssues(company)) {
                metaView.updateProductsByPlatformIds(new HashSet<>());
            } else {
                resetProduct(meta, metaView);
                updateProductsFilter(
                        metaView,
                        meta.getPlatformId() == null ?
                                toSet(emptyIfNull(platformOptions), PlatformOption::getId) :
                                new HashSet<>(Collections.singleton(meta.getPlatformId())));
            }

            requestSla(meta.getPlatformId(), slaList -> fillSla(getSlaByImportanceLevel(slaList, metaView.importance().getValue())));
            onCaseMetaChanged(meta, () -> {
                fireEvent(new IssueEvents.ChangeIssue(meta.getId()));
                fireEvent(new CommentAndHistoryEvents.Reload());
            });
        });
    }

    private void requestPlatforms(Long companyId, Consumer<List<PlatformOption>> resultConsumer) {
        PlatformQuery query = new PlatformQuery();
        query.setCompanyId(companyId);

        siteFolderController.getPlatformsOptionList(query, new FluentCallback<List<PlatformOption>>()
                .withError(throwable -> resultConsumer.accept(null))
                .withSuccess(resultConsumer)
        );
    }

    private void updateProductsFilter(final AbstractIssueMetaView metaView, Long companyId, Long platformId) {
        if (platformId != null) {
            metaView.updateProductsByPlatformIds(new HashSet<>(Collections.singleton(platformId)));
        } else {
            requestPlatforms(companyId, platformOptions -> updateProductsFilter(metaView, toSet(emptyIfNull(platformOptions), PlatformOption::getId)));
        }
    }

    private void updateProductsFilter(final AbstractIssueMetaView metaView, Set<Long> platformIds) {
        if (isEmpty(platformIds)) {
            metaView.updateProductsByPlatformIds(null);
        } else {
            metaView.updateProductsByPlatformIds(platformIds);
            metaView.productEnabled().setEnabled(isProductEnabled(readOnly, currentCompany));
        }
    }

    private boolean isProductEnabled(boolean readOnly, Company company) {
        if (readOnly) {
            return false;
        }

        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRODUCT_EDIT)) {
            return true;
        }

        if (isCompanyWithAutoOpenIssues(company)) {
            return true;
        }

        if (policyService.isSubcontractorCompany()) {
            return true;
        }

        return false;
    }

    private boolean isCompanyWithAutoOpenIssues(Company company) {
        return Boolean.TRUE.equals(company.getAutoOpenIssue());
    }

    private void resetProduct(CaseObjectMeta meta, AbstractIssueMetaView metaView) {
        meta.setProduct(null);
        metaView.product().setValue(null);
    }

    private void setCurrentCompany(Company company) {
        this.currentCompany = company;
    }

    private void updateProductModelAndMandatory(AbstractIssueMetaView metaView, boolean isCompanyWithAutoOpenIssues) {
        metaView.setProductModel(isCompanyWithAutoOpenIssues ? productWithChildrenModel : productModel);
        metaView.setProductMandatory(isCompanyWithAutoOpenIssues);
    }

    private boolean isSubcontractorCompany(Company userCompany) {
        return userCompany.getCategory() == En_CompanyCategory.SUBCONTRACTOR;
    }

    private boolean isCustomer() {
        return !policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW);
    }

    private void onParentIssueChanged(Long caseId) {
        caseLinkController.getCaseLinks(caseId, new FluentCallback<List<CaseLink>>()
                .withSuccess(links ->
                        links.stream()
                                .filter(caseLink -> Objects.equals(caseLink.getBundleType(), En_BundleType.SUBTASK))
                                .forEach(
                                        caseLink -> fireEvent(new IssueEvents.ChangeIssue(caseLink.getCaseInfo().getId()))
                                )));
    }

    private ProjectSla makeDefaultProjectSla(ImportanceLevel importanceLevel) {
        return new ProjectSla(importanceLevel.getId(), importanceLevel.getReactionTime(),
                importanceLevel.getTemporarySolutionTime(), importanceLevel.getFullSolutionTime());
    }

    @Inject
    AbstractIssueMetaView metaView;

    @Inject
    CaseStateFilterProvider caseStateFilter;

    @Inject
    Lang lang;

    @Inject
    PolicyService policyService;
    @Inject
    HomeCompanyService homeCompanyService;

    @Inject
    IssueControllerAsync issueController;
    @Inject
    CompanyControllerAsync companyController;
    @Inject
    SLAControllerAsync slaController;
    @Inject
    SiteFolderControllerAsync siteFolderController;
    @Inject
    CaseLinkControllerAsync caseLinkController;
    @Inject
    CaseStateControllerAsync caseStateController;

    @Inject
    ProductModel productModel;
    @Inject
    ProductWithChildrenModel productWithChildrenModel;

    @Inject
    CompanyModel companyModel;
    @Inject
    SubcontractorCompanyModel subcontractorCompanyModel;
    @Inject
    CustomerCompanyModel customerCompanyModel;
    @Inject
    ImportanceLevelControllerAsync importanceService;
    @Inject
    En_ResultStatusLang resultStatusLang;
    @Inject
    En_IssueValidationResultLang validationResultLang;
    @Inject
    DefaultErrorHandler defaultErrorHandler;
    @Inject
    ConfigStorage configStorage;

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
    private Company currentCompany;

    private static final Logger log = Logger.getLogger(IssueMetaActivity.class.getName());
}
