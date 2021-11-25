package ru.protei.portal.ui.report.client.activity.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.dto.ReportDto;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ReportControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ru.protei.portal.core.model.dict.En_ReportStatus.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.setOf;
import static ru.protei.portal.ui.report.client.util.AccessUtil.canView;

public abstract class ReportTableActivity implements
        AbstractReportTableActivity,
        AbstractPagerActivity, Activity {

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();
        view.setActivity(this);
        pagerView.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(ReportEvents.Show event) {
        if (!canView(policyService)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }

        showActionBarButtons();
        showView();
        loadTable();
        fireChangeTimer();
    }

    private void showActionBarButtons() {
        fireEvent(new ActionBarEvents.Clear());
        fireEvent(new ActionBarEvents.Add(CREATE_ACTION, null, UiConstants.ActionBarIdentity.ISSUE_REPORT));
    }

    private void showView() {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        view.getPagerContainer().add(pagerView.asWidget());
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (event.isNot(UiConstants.ActionBarIdentity.ISSUE_REPORT)) {
            return;
        }
        fireEvent(new ReportEvents.Create());
    }

    @Override
    public void onRemoveClicked(ReportDto value) {
        if (value == null || value.getReport() == null || value.getReport().getId() == null) {
            return;
        }
        reportService.removeReports(setOf(value.getReport().getId()), null, new FluentCallback<List<Long>>()
                .withErrorMessage(lang.issueReportsNotDeleted())
                .withSuccess(ids -> {
                    fireEvent(new NotifyEvents.Show(lang.issueReportsDeleted(), NotifyEvents.NotifyType.SUCCESS));
                    loadTable();
                }));
    }

    @Override
    public void onDownloadClicked(ReportDto value) {
        if (value == null || value.getReport() == null || value.getReport().getId() == null) {
            return;
        }
        Window.open(GWT.getModuleBaseURL() + "download/report?id=" + value.getReport().getId().toString(), "_blank", "");
    }

    @Override
    public void onRefreshClicked(ReportDto value) {
        if (value == null || value.getReport() == null || value.getReport().getId() == null) {
            return;
        }
        reportService.recreateReport(value.getReport().getId(), new FluentCallback<Long>()
                .withSuccess(id -> {
                    fireEvent(new NotifyEvents.Show(lang.reportRequested(), NotifyEvents.NotifyType.SUCCESS));
                    loadTable();
                    fireChangeTimer();
                }));
    }

    @Override
    public void onCancelClicked(ReportDto value) {
        if (value == null || value.getReport() == null || value.getReport().getId() == null) {
            return;
        }
        reportService.cancelReport(value.getReport().getId(), new FluentCallback<Long>()
                .withSuccess(id -> {
                    fireEvent(new NotifyEvents.Show(lang.reportCanceled(id), NotifyEvents.NotifyType.SUCCESS));
                    loadTable();
                    timer.cancel();
                }));
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<ReportDto>> asyncCallback) {
        boolean isFirstChunk = offset == 0;
        ReportQuery query = getQuery();
        query.setOffset(offset);
        query.setLimit(limit);
        reportService.getReportsByQuery(query, new FluentCallback<SearchResult<ReportDto>>()
                .withError(throwable -> {
                    defaultErrorHandler.accept(throwable);
                    asyncCallback.onFailure(throwable);
                })
                .withSuccess(sr -> {
                    if (isFirstChunk) {
                        view.setTotalRecords(sr.getTotalCount());
                        pagerView.setTotalPages(view.getPageCount());
                        pagerView.setTotalCount(sr.getTotalCount());
                    }
                    tableReports = sr.getResults();
                    asyncCallback.onSuccess(sr.getResults());
                }));
    }

    @Override
    public void onPageChanged(int page) {
        pagerView.setCurrentPage(page);
    }

    @Override
    public void onPageSelected(int page) {
        view.scrollTo(page);
    }

    @Override
    public void onEditClicked( ReportDto value ) {
        fireEvent(new ReportEvents.Edit(value.getReport().getId(), value));
    }

    private void loadTable() {
        view.clearRecords();
        view.triggerTableLoad();
    }

    private ReportQuery getQuery() {
        ReportQuery query = new ReportQuery();
        query.setSortField(En_SortField.creation_date);
        query.setSortDir(En_SortDir.DESC);
        return query;
    }

    private void fireChangeTimer() {
        timer.cancel();
        timer.scheduleRepeating(1000);
    }

    Timer timer = new Timer() {
        @Override
        public void run() {
            if (view.isAttached()) {
                Set<Long> notReadyReportsIds = new HashSet<>();
                for (ReportDto reportDto: tableReports) {
                    Report report = reportDto.getReport();
                    if (report.getStatus().equals(CREATED) ||
                        report.getStatus().equals(PROCESS)) {
                        notReadyReportsIds.add(report.getId());
                    }
                }

                if (notReadyReportsIds.isEmpty()) {
                    timer.cancel();
                } else {
                    ReportQuery query = new ReportQuery();
                    query.setIncludeIds(notReadyReportsIds);
                    reportService.getReportsByQuery(query, new FluentCallback<SearchResult<ReportDto>>()
                                 .withError(throwable -> {
                                     defaultErrorHandler.accept(throwable);
                                 })
                                 .withSuccess(sr -> {
                                     tableReports = sr.getResults();
                                     for (ReportDto reportDto: tableReports) {
                                         view.updateRow(reportDto);
                                     }
                                 }));
                }
            }
        }
    };

    @Inject
    AbstractReportTableView view;
    @Inject
    ReportControllerAsync reportService;
    @Inject
    Lang lang;
    @Inject
    AbstractPagerView pagerView;
    @Inject
    PolicyService policyService;
    @Inject
    protected DefaultErrorHandler defaultErrorHandler;

    private List<ReportDto> tableReports;
    private AppEvents.InitDetails initDetails;
    private static String CREATE_ACTION;
}
