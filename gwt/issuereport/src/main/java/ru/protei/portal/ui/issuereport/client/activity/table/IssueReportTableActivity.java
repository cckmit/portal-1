package ru.protei.portal.ui.issuereport.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ReportServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Collections;
import java.util.List;

public abstract class IssueReportTableActivity implements
        AbstractIssueReportTableActivity,
        AbstractPagerActivity, Activity
{

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();

        view.setActivity(this);
        view.setAnimation(animation);

        pagerView.setActivity(this);
    }

    @Event
    public void onShow(IssueReportEvents.Show event) {
        fireEvent(new AppEvents.InitPanelName(lang.issueReports()));

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        initDetails.parent.add(pagerView.asWidget());

        fireEvent(policyService.hasPrivilegeFor(En_Privilege.ISSUE_EXPORT) ?
                new ActionBarEvents.Add(CREATE_ACTION, UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.ISSUE_REPORTS) :
                new ActionBarEvents.Clear()
        );

        requestReportsCount();
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.ISSUE_REPORTS.equals(event.identity)) {
            return;
        }

        //fireEvent(new IssueReportEvents.Edit());
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Override
    public void onItemClicked(Report value) {}

    @Override
    public void onEditClicked(Report value) {}

    @Override
    public void onRemoveClicked(Report value) {
        if (value.getId() == null) {
            return;
        }
        reportService.removeReports(Collections.singleton(value.getId()), null, new RequestCallback<Void>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.issueReportsNotDeleted(), NotifyEvents.NotifyType.ERROR));

            }

            @Override
            public void onSuccess(Void result) {
                fireEvent(new NotifyEvents.Show(lang.issueReportsDeleted(), NotifyEvents.NotifyType.SUCCESS));
            }
        });
    }

    @Override
    public void onDownloadClicked(Report value) {
        if (value.getId() == null) {
            return;
        }
        Window.open("/Crm/download/report?id=" + value.getId().toString(), "_blank", "");
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<Report>> asyncCallback) {
        ReportQuery query = getQuery();
        query.setOffset(offset);
        query.setLimit(limit);
        reportService.getReportsByQuery(query, new RequestCallback<List<Report>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                asyncCallback.onFailure(throwable);
            }

            @Override
            public void onSuccess(List<Report> result) {
                asyncCallback.onSuccess(result);
            }
        });
    }

    @Override
    public void onPageChanged(int page) {
        pagerView.setCurrentPage(page + 1);
    }

    @Override
    public void onFirstClicked() {
        view.scrollTo(0);
    }

    @Override
    public void onLastClicked() {
        view.scrollTo(view.getPageCount() - 1);
    }

    private void requestReportsCount() {
        view.clearRecords();
        animation.closeDetails();
        reportService.getReportsCount(getQuery(), new RequestCallback<Long>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Long result) {
                view.setReportsCount(result);
                pagerView.setTotalPages(view.getPageCount());
                pagerView.setTotalCount(result);
            }
        });
    }

    private ReportQuery getQuery() {
        return new ReportQuery();
    }

    @Inject
    AbstractIssueReportTableView view;

    @Inject
    PolicyService policyService;

    @Inject
    ReportServiceAsync reportService;

    @Inject
    Lang lang;

    @Inject
    TableAnimation animation;

    @Inject
    AbstractPagerView pagerView;

    private static String CREATE_ACTION;
    private AppEvents.InitDetails initDetails;
}
