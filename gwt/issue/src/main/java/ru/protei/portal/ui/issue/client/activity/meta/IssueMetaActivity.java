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
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DefaultSlaValues;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.*;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductWithChildrenModel;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;
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
        if (CrmConstants.State.CREATED == metaView.state().getValue().getId() && meta.getManager() != null){
            fireEvent(new NotifyEvents.Show(lang.errSaveIssueNeedUnselectManager(), NotifyEvents.NotifyType.ERROR));
            metaView.state().setValue(new CaseState(meta.getStateId(), meta.getStateName()));
            return;
        }

        meta.setStateId(metaView.state().getValue().getId());
        meta.setPauseDate((CrmConstants.State.PAUSED != meta.getStateId() || metaView.pauseDate().getValue() == null) ? null : metaView.pauseDate().getValue().getTime());

        setManagerCompanyEnabled(metaView, metaView.state().getValue().getId());
        metaView.pauseDateContainerVisibility().setVisible(CrmConstants.State.PAUSED == meta.getStateId());
        metaView.pauseDate().setValue(CrmConstants.State.PAUSED != meta.getStateId() ? null : metaView.pauseDate().getValue());

        if (!isPauseDateValid(meta.getStateId(), meta.getPauseDate())) {
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
        meta.setProduct(DevUnit.fromProductShortView(metaView.product().getValue()));
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

        Runnable onResetProduct = null;

        if (isCompanyWithAutoOpenIssues(currentCompany)) {
            resetProduct(meta, metaView);
            updateProductsFilter(metaView, meta.getInitiatorCompanyId(), meta.getPlatformId());

            onResetProduct = () -> fireEvent(new IssueEvents.ChangeIssue(meta.getId()));
        }

        onCaseMetaChanged(meta, onResetProduct);
        requestSla(meta.getPlatformId(), slaList -> fillSla(getSlaByImportanceLevel(slaList, meta.getImpLevel())));
    }

    @Override
    public void onTimeElapsedChanged() {
        meta.setTimeElapsed(metaView.getTimeElapsed());
        onCaseMetaChanged( meta );
    }

    @Override
    public void onPauseDateChanged() {
        if (!isPauseDateValid(meta.getStateId(), metaView.pauseDate().getValue() == null ? null : metaView.pauseDate().getValue().getTime())) {
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

        companyService.getCompany(selectedCompanyId, new FluentCallback<Company>()
                .withSuccess(resultCompany -> {
                    setCurrentCompany(resultCompany);
                    fillPlatformValueAndUpdateProductsFilter(resultCompany);
                })
        );
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
                    meta.setStateId(caseMetaUpdated.getStateId());
                    meta.setStateName(caseMetaUpdated.getStateName());
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
        metaView.productEnabled().setEnabled(isProductEnabled(readOnly, meta.getInitiatorCompany()) );
        metaView.companyEnabled().setEnabled(!readOnly && isCompanyChangeAllowed(meta.isPrivateCase()) );
        metaView.initiatorEnabled().setEnabled(!readOnly && isInitiatorChangeAllowed(meta.getInitiatorCompanyId()));
        metaView.platformEnabled().setEnabled(!readOnly);

        metaView.timeElapsedHeaderVisibility().setVisible(true);

        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW)) { //TODO change rule
            metaView.caseSubscriptionContainer().setVisible(true);
        } else {
            metaView.caseSubscriptionContainer().setVisible(false);
        }

        metaView.importance().setValue( meta.getImportance() );
        metaView.setStateWorkflow(recognizeWorkflow(meta.getExtAppType()));//Обязательно сетить до установки значения!
        metaView.state().setValue(new CaseState(meta.getStateId(), meta.getStateName()));
        metaView.pauseDate().setValue(meta.getPauseDate() == null ? null : new Date(meta.getPauseDate()));
        metaView.pauseDateContainerVisibility().setVisible(CrmConstants.State.PAUSED == meta.getStateId());
        metaView.setPauseDateValid(isPauseDateValid(meta.getStateId(), meta.getPauseDate()));

        metaView.timeElapsedContainerVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW));
        metaView.timeElapsedEditContainerVisibility().setVisible(false);
        metaView.setTimeElapsed(meta.getTimeElapsed());

        this.currentCompany = meta.getInitiatorCompany();

        metaView.setCompany(meta.getInitiatorCompany());
        metaView.initiatorUpdateCompany(meta.getInitiatorCompany());
        metaView.setInitiator(meta.getInitiator());

        metaView.setPlatformFilter(platformOption -> meta.getInitiatorCompanyId().equals(platformOption.getCompanyId()));
        setPlatformVisibility(metaView, policyService.hasPrivilegeFor(En_Privilege.ISSUE_PLATFORM_EDIT));

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

        metaView.platform().setValue( meta.getPlatformId() == null ? null : new PlatformOption(meta.getPlatformName(), meta.getPlatformId()) );

        metaView.setProductMandatory(isCustomerWithAutoOpenIssues(meta.getInitiatorCompany()));

        metaView.product().setValue(ProductShortView.fromProduct(meta.getProduct()));

        if (!isCompanyWithAutoOpenIssues(meta.getInitiatorCompany())) {
            metaView.setProductModel(productModel);
        } else {
            metaView.setProductModel(productWithChildrenModel);
            updateProductsFilter(metaView, meta.getInitiatorCompanyId(), meta.getPlatformId());
        }

        metaView.setJiraInfoLink(LinkUtils.makeJiraInfoLink());

        metaView.slaContainerVisibility().setVisible(!isJiraIssue() && isSystemScope());
        requestSla(meta.getPlatformId(), slaList -> fillSla(getSlaByImportanceLevel(slaList, meta.getImpLevel())));
    }

    private void fillManagerInfoContainer(final AbstractIssueMetaView issueMetaView, CaseObjectMeta caseObjectMeta, boolean isReadOnly) {
        issueMetaView.managerEnabled().setEnabled(!isReadOnly && policyService.hasPrivilegeFor(En_Privilege.ISSUE_MANAGER_EDIT));
        setManagerCompanyEnabled(issueMetaView, caseObjectMeta.getStateId());

        if (caseObjectMeta.getManagerCompanyId() != null) {
            issueMetaView.setManagerCompany(new EntityOption(caseObjectMeta.getManagerCompanyName(), caseObjectMeta.getManagerCompanyId()));
            issueMetaView.updateManagersCompanyFilter(caseObjectMeta.getManagerCompanyId());
        } else {
            homeCompanyService.getHomeCompany(CrmConstants.Company.HOME_COMPANY_ID, company -> {
                issueMetaView.setManagerCompany(company);
                issueMetaView.updateManagersCompanyFilter(company.getId());
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

        boolean managerIsValid = caseMeta.getManager() != null || !isStateWithRestrictions(caseMeta.getStateId());
        metaView.managerValidator().setValid(managerIsValid);

        boolean productIsValid = isProductValid(caseMeta);

        metaView.productValidator().setValid(productIsValid);

        boolean isFieldsValid =
                        productIsValid &&
                        managerIsValid &&
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

        if (isCustomerWithAutoOpenIssues(currentCompany)) {
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
        metaView.companyEnabled().setEnabled(!readOnly && isCompanyChangeAllowed( meta.isPrivateCase()));
    }

    private boolean isStateWithRestrictions(long caseStateId) {
        return CrmConstants.State.CREATED != caseStateId &&
                CrmConstants.State.CANCELED != caseStateId;
    }

    private String transliteration(String input) {
        return TransliterationUtils.transliterate(input, LocaleInfo.getCurrentLocale().getLocaleName());
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

    private boolean isSystemScope() {
        return policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW);
    }

    private void setPlatformVisibility(AbstractIssueMetaView issueMetaView, boolean isVisible) {
        issueMetaView.platformVisibility().setVisible(isVisible);
        issueMetaView.setInitiatorBorderBottomVisible(!isVisible);
    }

    private void setManagerCompanyEnabled(AbstractIssueMetaView issueMetaView, Long stateId) {
        issueMetaView.managerCompanyEnabled().setEnabled(policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_EDIT) && stateId == CrmConstants.State.CUSTOMER_RESPONSIBILITY);
    }

    private void fillPlatformValueAndUpdateProductsFilter(Company company) {
        requestPlatforms(company.getId(), platformOptions -> {
            if (platformOptions != null && platformOptions.size() == 1) {
                metaView.platform().setValue(platformOptions.get(0));
                meta.setPlatform(platformOptions.get(0));
            } else {
                metaView.platform().setValue(null);
                meta.setPlatform(null);
            }

            if (!isCompanyWithAutoOpenIssues(company)) {
                metaView.setProductModel(productModel);
            } else {
                metaView.setProductModel(productWithChildrenModel);
                resetProduct(meta, metaView);
                updateProductsFilter(
                        metaView,
                        meta.getPlatformId() == null ?
                                toSet(emptyIfNull(platformOptions), PlatformOption::getId) :
                                new HashSet<>(Collections.singleton(meta.getPlatformId())));
            }

            requestSla(meta.getPlatformId(), slaList -> fillSla(getSlaByImportanceLevel(slaList, meta.getImpLevel())));
            onCaseMetaChanged(meta, () -> fireEvent(new IssueEvents.ChangeIssue(meta.getId())));
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
            metaView.productEnabled().setEnabled(false);
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

        return false;
    }

    private boolean isCustomerWithAutoOpenIssues(Company company) {
        if (hasSystemScopeForEdit()) {
            return false;
        }

        if (!isCompanyWithAutoOpenIssues(company)) {
            return false;
        }

        return true;
    }

    private boolean hasSystemScopeForEdit() {
        return policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_EDIT);
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

    private ProductModel getProductModelByCompany(Company company) {
        return isCompanyWithAutoOpenIssues(company) ? productWithChildrenModel : productModel;
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
    @Inject
    ProductModel productModel;
    @Inject
    ProductWithChildrenModel productWithChildrenModel;

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

    private static final Logger log = Logger.getLogger( IssueMetaActivity.class.getName());

}
