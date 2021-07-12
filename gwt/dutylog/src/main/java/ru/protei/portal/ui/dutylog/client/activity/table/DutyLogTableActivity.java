package ru.protei.portal.ui.dutylog.client.activity.table;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.DutyLog;
import ru.protei.portal.core.model.query.DutyLogQuery;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DutyLogControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

import static ru.protei.portal.ui.common.client.util.PaginationUtils.PAGE_SIZE;


public abstract class DutyLogTableActivity
        implements AbstractDutyLogTableActivity, AbstractPagerActivity,
        Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        pagerView.setActivity(this);
        view.getPagerContainer().add(pagerView.asWidget());
    }

    @Event
    public void onAppInit ( AppEvents.InitDetails event ) {
        this.init = event;
    }

    @Event
    public void onAuthSuccess ( AuthEvents.Success event ) {
        if (!policyService.hasPrivilegeFor(En_Privilege.DUTY_LOG_VIEW)) {
            return;
        }

        view.getFilterWidget().resetFilter();
    }

    @Event
    public void onShow(DutyLogEvents.Show event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.DUTY_LOG_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(init.parent));
            return;
        }

        view.clearRecords();

        init.parent.clear();
        init.parent.add(view.asWidget());

        fireEvent(policyService.hasPrivilegeFor(En_Privilege.DUTY_LOG_CREATE) ?
                new ActionBarEvents.Add(lang.buttonCreate(), null, UiConstants.ActionBarIdentity.DUTY_LOG) :
                new ActionBarEvents.Clear()
        );

        if (policyService.hasPrivilegeFor(En_Privilege.DUTY_LOG_REPORT)) {
            fireEvent(new ActionBarEvents.Add(lang.buttonReport(), "", UiConstants.ActionBarIdentity.DUTY_LOG_CREATE_REPORT));
        }

        requestData(0);
    }

    @Event
    public void onCreateClicked( ActionBarEvents.Clicked event ) {
        if (!UiConstants.ActionBarIdentity.DUTY_LOG.equals(event.identity)) {
            return;
        }

        fireEvent(new DutyLogEvents.Edit());
    }

    @Event
    public void onUpdate(DutyLogEvents.Update event) {
        if (view.asWidget().isAttached()) {
            requestData(page);
        }
    }

    @Event
    public void onDutyLogReportClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.DUTY_LOG_CREATE_REPORT.equals(event.identity)) {
            return;
        }

        if (!policyService.hasPrivilegeFor(En_Privilege.DUTY_LOG_REPORT)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        fireEvent(new DutyLogEvents.CreateReport());
    }

    @Override
    public void onItemClicked(DutyLog value) {}

    @Override
    public void onEditClicked(DutyLog value) {
        fireEvent(new DutyLogEvents.Edit(value.getId()));
    }

    @Override
    public void onFilterChange() {
        this.page = 0;
        requestData( this.page );
    }

    @Override
    public void onPageSelected( int page ) {
        this.page = page;
        requestData( this.page );
    }

    private DutyLogQuery fillQuery() {
        return view.getFilterWidget().getFilterParamView().getQuery();
    }

    private void requestData(int page) {
        view.clearRecords();
        DutyLogQuery query = fillQuery();

        boolean isFirstChunk = page == 0;
        query.setOffset( page * PAGE_SIZE );
        query.setLimit( PAGE_SIZE );

        dutyLogController.getDutyLogs(query, new FluentCallback<SearchResult<DutyLog>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(dutyLogs -> {
                    view.addRecords(dutyLogs.getResults());
                    if (isFirstChunk) {
                        pagerView.setTotalCount(dutyLogs.getTotalCount());
                    }
                }));
    }

    @Inject
    private Lang lang;

    @Inject
    private AbstractPagerView pagerView;
    @Inject
    private AbstractDutyLogTableView view;

    @Inject
    private PolicyService policyService;
    @Inject
    private DutyLogControllerAsync dutyLogController;

    private AppEvents.InitDetails init;

    private int page;
}
