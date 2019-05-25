package ru.protei.portal.ui.issuereport.client.activity.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.IssueReportEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ReportControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

public abstract class IssueReportTableActivity implements
        AbstractIssueReportTableActivity,
        AbstractPagerActivity, Activity
{

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();
        view.setActivity(this);
        pagerView.setActivity(this);
    }

    @Event
    public void onShow(IssueReportEvents.Show event) {
        fireEvent(new AppEvents.InitPanelName(lang.issueReports()));

        fireEvent(new ActionBarEvents.Clear());
        fireEvent(new ActionBarEvents.Add(CREATE_ACTION, null, UiConstants.ActionBarIdentity.ISSUE_REPORT));

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        view.getPagerContainer().add( pagerView.asWidget() );

        loadTable();
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (event.isNot(UiConstants.ActionBarIdentity.ISSUE_REPORT)) {
            return;
        }
        fireEvent(new IssueReportEvents.Create());
    }

    @Override
    public void onRemoveClicked(Report value) {
        if (value.getId() == null) {
            return;
        }
        reportService.removeReports(new HashSet<>(Collections.singletonList(value.getId())), null, new RequestCallback<Void>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.issueReportsNotDeleted(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Void result) {
                fireEvent(new NotifyEvents.Show(lang.issueReportsDeleted(), NotifyEvents.NotifyType.SUCCESS));
                fireEvent(new IssueReportEvents.Show());
            }
        });
    }

    @Override
    public void onDownloadClicked(Report value) {
        if (value.getId() == null) {
            return;
        }
        Window.open(GWT.getModuleBaseURL() + "download/report?id=" + value.getId().toString(), "_blank", "");
    }

    @Override
    public void onRefreshClicked(Report value) {
        if (!value.isAllowedRefresh()) {
            return;
        }
        reportService.recreateReport(value.getId(), new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(throwable.getMessage(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Void result) {
                fireEvent(new NotifyEvents.Show(lang.reportRequested(), NotifyEvents.NotifyType.SUCCESS));
                fireEvent(new IssueReportEvents.Show());
            }
        });
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<Report>> asyncCallback) {
        boolean isFirstChunk = offset == 0;
        ReportQuery query = getQuery();
        query.setOffset(offset);
        query.setLimit(limit);
        reportService.getReportsByQuery(query, new FluentCallback<SearchResult<Report>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    asyncCallback.onFailure(throwable);
                })
                .withSuccess(sr -> {
                    asyncCallback.onSuccess(sr.getResults());
                    if (isFirstChunk) {
                        view.setTotalRecords(sr.getTotalCount());
                        pagerView.setTotalPages(view.getPageCount());
                        pagerView.setTotalCount(sr.getTotalCount());
                    }
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

    @Inject
    AbstractIssueReportTableView view;
    @Inject
    ReportControllerAsync reportService;
    @Inject
    Lang lang;
    @Inject
    AbstractPagerView pagerView;

    private AppEvents.InitDetails initDetails;
    private static String CREATE_ACTION;
}
