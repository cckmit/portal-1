package ru.protei.portal.ui.issuereport.client.activity.create;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.client.service.ReportControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.issuereport.client.widget.issuefilter.model.AbstractIssueFilterActivity;

import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public abstract class IssueReportCreateActivity implements Activity,
        AbstractIssueReportCreateActivity, AbstractDialogDetailsActivity, AbstractIssueFilterActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.getIssueFilter().setActivity(this);
        dialogView.setActivity(this);
        dialogView.setHeader(lang.issueReportNew());
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        view.fillReportTypes(makeReportTypeList());
    }

    @Event
    public void onShow(IssueReportEvents.Create event) {
        isSaving = false;
        view.resetFilter();
        applyIssueFilterViewByPrivileges();
        dialogView.showPopup();
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
                    view.getIssueFilter().resetFilter();
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
        Report report = new Report();
        report.setReportType(reportType);
        report.setName(view.name().getValue());
        report.setLocale(LocaleInfo.getCurrentLocale().getLocaleName());
        CaseQuery query = view.getIssueFilter().getValue();
        if (query == null) return;
        report.setCaseQuery( query );

        if (isSaving) {
            return;
        }
        isSaving = true;

        reportController.createReport(report, new FluentCallback<Long>()
                .withResult(() -> isSaving = false)
                .withSuccess(result -> {
                    dialogView.hidePopup();
                    fireEvent(new NotifyEvents.Show(lang.reportRequested(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IssueReportEvents.Show());
                })
        );
    }

    @Override
    public void onCancelClicked() {
        dialogView.hidePopup();
    }

    @Override
    public void onReportTypeSelected() {
        applyIssueFilterViewByPrivileges();
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

        filterService.saveIssueFilter(caseFilter, new FluentCallback<CaseFilter>()
                .withErrorMessage(lang.errSaveIssueFilter())
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

    @Override
    public boolean validateQuery(En_CaseFilterType filterType, CaseQuery query) {
        switch (filterType) {
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

    private void applyIssueFilterViewByPrivileges() {
        switch (view.getIssueFilter().getFilterType()) {
            case CASE_OBJECTS:
                view.getIssueFilter().companiesVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_COMPANY_VIEW));
                view.getIssueFilter().productsVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_PRODUCT_VIEW));
                view.getIssueFilter().managersVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW));
                view.getIssueFilter().searchPrivateVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW));
                break;
            case CASE_TIME_ELAPSED:
                view.getIssueFilter().companiesVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_COMPANY_VIEW));
                view.getIssueFilter().productsVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_PRODUCT_VIEW));
                break;
            case CASE_RESOLUTION_TIME:
                view.getIssueFilter().companiesVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_COMPANY_VIEW));
                view.getIssueFilter().productsVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_PRODUCT_VIEW));
                view.getIssueFilter().managersVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW));
                break;
        }
    }

    private List<En_ReportType> makeReportTypeList() {
        if (policyService.hasGrantAccessFor(En_Privilege.ISSUE_REPORT)) {
            return Arrays.asList(En_ReportType.values());
        }
        return Arrays.asList(En_ReportType.CASE_OBJECTS);
    }

    @Inject
    Lang lang;
    @Inject
    AbstractIssueReportCreateView view;
    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    ReportControllerAsync reportController;
    @Inject
    PolicyService policyService;

    @Inject
    IssueFilterControllerAsync filterService;

    private Long filterIdToRemove;
    private boolean isSaving;
    private Set<En_CaseState> activeStates = new HashSet<>( Arrays.asList( En_CaseState.CREATED, En_CaseState.OPENED,
            En_CaseState.ACTIVE, En_CaseState.TEST_LOCAL, En_CaseState.WORKAROUND ) );
}
