package ru.protei.portal.ui.issuereport.client.activity.create;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.client.service.ReportControllerAsync;
import ru.protei.portal.ui.common.client.util.IssueFilterUtils;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.issuereport.client.widget.issuefilter.model.AbstractIssueFilterModel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

public abstract class IssueReportCreateActivity implements Activity,
        AbstractIssueReportCreateActivity, AbstractIssueFilterModel {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.getIssueFilter().setModel(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        view.fillReportTypes(makeReportTypeList());
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(IssueReportEvents.Create event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.ISSUE_REPORT)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        isSaving = false;
        view.reset();

        if(!policyService.hasSystemScopeForPrivilege(En_Privilege.COMPANY_VIEW)){
            HashSet<EntityOption> companyIds = new HashSet<>();
            companyIds.add(IssueFilterUtils.toEntityOption(policyService.getProfile().getCompany()));
            view.getIssueFilter().companies().setValue(companyIds);
            view.getIssueFilter().updateInitiators();
        }
    }

    @Event
    public void onConfirmRemoveUserFilter(ConfirmDialogEvents.Confirm event) {
        if (!event.identity.equals(getClass().getName()) || filterIdToRemove == null) {
            return;
        }
        filterService.removeIssueFilter(filterIdToRemove, new FluentCallback<Boolean>()
                .withError(throwable -> {
                    filterIdToRemove = null;
                    fireEvent(new NotifyEvents.Show(lang.errNotRemoved(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(aBoolean -> {
                    filterIdToRemove = null;
                    fireEvent(new NotifyEvents.Show(lang.issueFilterRemoveSuccessed(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IssueEvents.ChangeUserFilterModel());
                    view.getIssueFilter().reset();
                }));
    }

    @Event
    public void onCancelRemoveUserFilter(ConfirmDialogEvents.Cancel event) {
        if (!event.identity.equals(getClass().getName())) {
            return;
        }
        filterIdToRemove = null;
    }

    @Override
    public void onSaveClicked() {

        En_ReportType reportType = view.reportType().getValue();
        CaseQuery query = view.getIssueFilter().getValue();

        if (!validateQuery(reportType, query)) {
            return;
        }

        Report report = new Report();
        report.setReportType(reportType);
        report.setName(view.name().getValue());
        report.setLocale(LocaleInfo.getCurrentLocale().getLocaleName());
        report.setCaseQuery(query);

        if (isSaving) {
            return;
        }
        isSaving = true;

        reportController.createReport(report, new FluentCallback<Long>()
                .withError(t -> isSaving = false)
                .withSuccess(result -> {
                    isSaving = false;
                    fireEvent(new Back());
                    fireEvent(new NotifyEvents.Show(lang.reportRequested(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IssueReportEvents.Show());
                })
        );
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    @Override
    public void onReportTypeChanged() {
        applyIssueFilterVisibilityByPrivileges();
    }

    @Override
    public void onUserFilterChanged(Long id, Consumer<CaseFilter> consumer) {

        filterService.getIssueFilter(id, new FluentCallback<CaseFilter>()
                .withErrorMessage(lang.errNotFound())
                .withSuccess(caseFilter ->
                    consumer.accept(caseFilter)));
    }

    @Override
    public void onSaveFilterClicked(CaseFilter caseFilter, Consumer<CaseFilterShortView> consumer) {

        if (!validateQuery(caseFilter.getType(), caseFilter.getParams())) {
            return;
        }

        filterService.saveIssueFilter(caseFilter, new FluentCallback<CaseFilter>()
                .withError(throwable -> {
                    defaultErrorHandler.accept(throwable);
                    fireEvent(new NotifyEvents.Show(lang.errSaveIssueFilter(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(filter -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IssueEvents.ChangeUserFilterModel());
                    consumer.accept(filter.toShortView());
                }));
    }

    @Override
    public void onRemoveFilterClicked(Long id) {
        filterIdToRemove = id;
        fireEvent(new ConfirmDialogEvents.Show(getClass().getName(), lang.issueFilterRemoveConfirmMessage()));
    }

    private boolean validateQuery( En_CaseFilterType filterType, CaseQuery query) {
        return validateQuery(En_ReportType.valueOf(filterType.name()), query);
    }

    private boolean validateQuery(En_ReportType reportType, CaseQuery query) {
        if (reportType == null || query == null) {
            return false;
        }

        switch (reportType) {
            case CASE_RESOLUTION_TIME:
                if (query.getCreatedFrom() == null || query.getCreatedTo() == null)  {
                    fireEvent(new NotifyEvents.Show(lang.reportMissingPeriod(), NotifyEvents.NotifyType.ERROR));
                    return false;
                }
                if (query.getStateIds() == null)  {
                    fireEvent(new NotifyEvents.Show( lang.reportMissingState(), NotifyEvents.NotifyType.ERROR));
                    return false;
                }
                break;
        }
        return true;
    }

    private void applyIssueFilterVisibilityByPrivileges() {
        if (view.getIssueFilter().productsVisibility().isVisible()) {
            view.getIssueFilter().productsVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_PRODUCT_VIEW));
        }
        if (view.getIssueFilter().managersVisibility().isVisible()) {
            view.getIssueFilter().managersVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW));
        }
        if (view.getIssueFilter().searchPrivateVisibility().isVisible()) {
            view.getIssueFilter().searchPrivateVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW));
        }
    }

    private List<En_ReportType> makeReportTypeList() {
        if (policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_REPORT)) {
            return Arrays.asList(En_ReportType.values());
        }
        return Arrays.asList(En_ReportType.CASE_OBJECTS);
    }

    @Inject
    Lang lang;
    @Inject
    AbstractIssueReportCreateView view;
    @Inject
    ReportControllerAsync reportController;
    @Inject
    PolicyService policyService;
    @Inject
    IssueFilterControllerAsync filterService;
    @Inject
    DefaultErrorHandler defaultErrorHandler;

    private Long filterIdToRemove;
    private boolean isSaving;
    private AppEvents.InitDetails initDetails;
}
