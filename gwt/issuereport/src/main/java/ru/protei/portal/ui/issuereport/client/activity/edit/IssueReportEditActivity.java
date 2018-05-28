package ru.protei.portal.ui.issuereport.client.activity.edit;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ReportStatus;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.IssueReportEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.En_ReportStatusLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ReportServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.issue.client.util.IssueFilterUtils;

import java.util.function.Consumer;

public abstract class IssueReportEditActivity implements AbstractIssueReportEditActivity, Activity {

    @PostConstruct
    public void init() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(IssueReportEvents.Edit event) {
        applyViewPrivileges();

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        if (event.reportId == null) {
            fireEvent(new AppEvents.InitPanelName(lang.issueReportsNew()));
            Report report = new Report();
            report.setStatus(En_ReportStatus.CREATED);
            report.setCaseQuery(new CaseQuery());
            report.setLocale(LocaleInfo.getCurrentLocale().getLocaleName());
            initialView(report);
        } else {
            fireEvent(new AppEvents.InitPanelName(lang.issueReportsView()));
            requestReport(event.reportId, this::initialView);
        }
    }

    @Override
    public void onRequestClicked() {
        if (report.getId() != null) {
            // read only mode
            return;
        }

        fillReportObject(report);

        reportService.createReport(report, new RequestCallback<Long>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(throwable.getMessage(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Long result) {
                fireEvent(new NotifyEvents.Show(lang.reportRequested(), NotifyEvents.NotifyType.SUCCESS));
                fireEvent(new Back());
            }
        });
    }

    @Override
    public void onDownloadClicked() {
        if (report.getId() == null || !report.isReady()) {
            return;
        }
        Window.open("/Crm/download/report?id=" + report.getId().toString(), "_blank", "");
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    private void requestReport(Long reportId, Consumer<Report> successAction) {
        reportService.getReport(reportId, new RequestCallback<Report>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(Report result) {
                successAction.accept(result);
            }
        });
    }

    private void initialView(Report report) {
        this.report = report;
        fillView(report);
    }

    private void fillView(Report report) {
        boolean notReadOnly = report.getId() == null;

        view.titleEnabled().setEnabled(notReadOnly);
        view.localeEnabled().setEnabled(notReadOnly);
        view.searchEnabled().setEnabled(notReadOnly);
        view.dateRangeEnabled().setEnabled(notReadOnly);
        view.sortFieldEnabled().setEnabled(notReadOnly);
        view.sortDirEnabled().setEnabled(notReadOnly);
        view.companiesEnabled().setEnabled(notReadOnly);
        view.productsEnabled().setEnabled(notReadOnly);
        view.managersEnabled().setEnabled(notReadOnly);
        view.importanceEnabled().setEnabled(notReadOnly);
        view.stateEnabled().setEnabled(notReadOnly);
        view.requestButtonVisibility().setVisible(notReadOnly);
        view.downloadButtonVisibility().setVisible(report.isReady());

        CaseQuery query = report.getCaseQuery();

        view.statusEnabled().setEnabled(false);
        view.status().setText(reportStatusLang.getStateName(report.getStatus()));
        view.title().setValue(report.getName());
        view.locale().setValue(report.getLocale());
        view.search().setText(query.getSearchString());
        view.dateRange().setValue(new DateInterval(query.getFrom(), query.getTo()));
        view.sortField().setValue(query.getSortField() != null ? query.getSortField() : En_SortField.creation_date);
        view.sortDir().setValue(query.getSortDir() == En_SortDir.ASC);
        view.companies().setValue(IssueFilterUtils.getCompanies(query.getCompanyIds()));
        view.managers().setValue(IssueFilterUtils.getManagers(query.getManagerIds()));
        view.products().setValue(IssueFilterUtils.getProducts(query.getProductIds()));
        view.importance().setValue(IssueFilterUtils.getImportances(query.getImportanceIds()));
        view.state().setValue(IssueFilterUtils.getStates(query.getStateIds()));
    }

    private void fillReportObject(Report report) {
        CaseQuery query = report.getCaseQuery();
        if (query == null) {
            query = new CaseQuery();
        }
        report.setName(view.title().getValue());
        report.setLocale(view.locale().getValue());
        query.setSearchString(view.search().getText());
        query.setSortField(view.sortField().getValue());
        query.setSortDir(view.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setCompanyIds(IssueFilterUtils.getCompaniesIdList(view.companies().getValue()));
        query.setProductIds(IssueFilterUtils.getProductsIdList(view.products().getValue()));
        query.setManagerIds(IssueFilterUtils.getManagersIdList(view.managers().getValue()));
        query.setImportanceIds(IssueFilterUtils.getImportancesIdList(view.importance().getValue()));
        query.setStates(IssueFilterUtils.getStateList(view.state().getValue()));
        DateInterval interval = view.dateRange().getValue();
        if (interval != null) {
            query.setFrom(interval.from);
            query.setTo(interval.to);
        }
        report.setCaseQuery(query);
    }

    private void applyViewPrivileges() {
        view.companiesVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_COMPANY_VIEW));
        view.productsVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_PRODUCT_VIEW));
        view.managersVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW));
    }

    @Inject
    Lang lang;

    @Inject
    En_ReportStatusLang reportStatusLang;

    @Inject
    ReportServiceAsync reportService;

    @Inject
    PolicyService policyService;

    @Inject
    AbstractIssueReportEditView view;

    private AppEvents.InitDetails initDetails;
    private Report report;
}
