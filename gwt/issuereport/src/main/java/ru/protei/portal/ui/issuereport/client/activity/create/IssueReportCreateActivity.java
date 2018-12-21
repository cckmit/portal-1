package ru.protei.portal.ui.issuereport.client.activity.create;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.IssueReportEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.client.service.ReportControllerAsync;
import ru.protei.portal.ui.common.client.util.IssueFilterUtils;
import ru.protei.portal.ui.common.client.widget.issuefilter.IssueFilterActivity;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

public abstract class IssueReportCreateActivity implements Activity,
        AbstractIssueReportCreateActivity, AbstractDialogDetailsActivity, IssueFilterActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this, this);
        dialogView.setActivity(this);
        dialogView.setHeader(lang.issueReportNew());
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Event
    public void onShow(IssueReportEvents.Create event) {
        isSaving = false;
        view.resetFilter();
        applyFilterViewPrivileges();
        dialogView.showPopup();
    }

    @Override
    public void onSaveClicked() {

        En_ReportType reportType = view.reportType().getValue();
        Report report = new Report();
        report.setReportType(reportType);
        report.setName(view.name().getValue());
        report.setLocale(LocaleInfo.getCurrentLocale().getLocaleName());
        report.setCaseQuery(makeCaseQuery());

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
        if (view.reportType().getValue() == null) {
            return;
        }
        switch (view.reportType().getValue()) {
            case CASE_OBJECTS: {
                view.commentAuthorsVisibility().setVisible(false);
                view.searchByCommentsVisibility().setVisible(true);
                break;
            }
            case CASE_TIME_ELAPSED: {
                view.commentAuthorsVisibility().setVisible(true);
                view.searchByCommentsVisibility().setVisible(false);
                break;
            }
        }
    }

    @Override
    public void onFilterChanged() {}

    @Override
    public void onCompaniesFilterChanged() {}

    @Override
    public void onUserFilterChanged() {

        CaseFilterShortView filter = view.userFilter().getValue();
        if (filter == null || filter.getId() == null) {
            view.resetFilter();
            view.toggleMsgSearchThreshold();
            return;
        }

        filterController.getIssueFilter(filter.getId(), new FluentCallback<CaseFilter>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errNotFound(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(caseFilter -> {
                    view.fillFilterFields(caseFilter.getParams());
                    view.toggleMsgSearchThreshold();
                })
        );
    }

    private void applyFilterViewPrivileges() {
        view.companiesVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_COMPANY_VIEW));
        view.productsVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_PRODUCT_VIEW));
        view.managersVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW));
        view.searchPrivateVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW));
    }

    private CaseQuery makeCaseQuery() {
        CaseQuery query = new CaseQuery();
        query.setType(En_CaseType.CRM_SUPPORT);
        query.setViewPrivate(view.searchPrivate().getValue());
        query.setSortField(view.sortField().getValue());
        query.setSortDir(view.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setCompanyIds(IssueFilterUtils.getCompaniesIdList(view.companies().getValue()));
        query.setProductIds(IssueFilterUtils.getProductsIdList(view.products().getValue()));
        query.setManagerIds(IssueFilterUtils.getManagersIdList(view.managers().getValue()));
        query.setInitiatorIds(IssueFilterUtils.getManagersIdList(view.initiators().getValue()));
        query.setImportanceIds(IssueFilterUtils.getImportancesIdList(view.importances().getValue()));
        query.setStates(IssueFilterUtils.getStateList(view.states().getValue()));
        query.setCommentAuthorIds(IssueFilterUtils.getManagersIdList(view.commentAuthors().getValue()));
        DateInterval interval = view.dateRange().getValue();
        if (interval != null) {
            query.setFrom(interval.from);
            query.setTo(interval.to);
        }
        String search = view.searchPattern().getValue();
        if (StringUtils.isBlank(search)) {
            query.setSearchString( null );
        } else {
            IssueFilterUtils.applyQuerySearch(query, search);
        }
        return query;
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
    IssueFilterControllerAsync filterController;
    @Inject
    PolicyService policyService;

    private boolean isSaving;
}
