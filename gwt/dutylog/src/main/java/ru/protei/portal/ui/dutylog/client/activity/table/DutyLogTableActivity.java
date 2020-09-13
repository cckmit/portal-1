package ru.protei.portal.ui.dutylog.client.activity.table;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.DutyLog;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.DutyLogQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DutyLogControllerAsync;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.dutylog.client.activity.filter.AbstractDutyLogFilterActivity;
import ru.protei.portal.ui.dutylog.client.activity.filter.AbstractDutyLogFilterView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Set;
import java.util.stream.Collectors;

import static ru.protei.portal.ui.common.client.util.PaginationUtils.PAGE_SIZE;
import static ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType.toDateRange;


public abstract class DutyLogTableActivity
        implements AbstractDutyLogTableActivity, AbstractDutyLogFilterActivity, AbstractPagerActivity,
        Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        pagerView.setActivity(this);
        filterView.setActivity(this);
        view.getFilterContainer().add(filterView.asWidget());
        view.getPagerContainer().add(pagerView.asWidget());
    }

    @Event
    public void onAppInit ( AppEvents.InitDetails event ) {
        this.init = event;
    }

    @Event
    public void onAuthSuccess ( AuthEvents.Success event ) {
        filterView.resetFilter();

        filterView.date().setValue(new DateIntervalWithType(null, En_DateIntervalType.THIS_WEEK));
    }

    @Event
    public void onShow(DutyLogEvents.Show event) {
        view.clearRecords();

        init.parent.clear();
        init.parent.add(view.asWidget());

        fireEvent(policyService.hasPrivilegeFor(En_Privilege.DUTY_LOG_CREATE) ?
                new ActionBarEvents.Add(lang.buttonCreate(), null, UiConstants.ActionBarIdentity.DUTY_LOG) :
                new ActionBarEvents.Clear()
        );

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

    @Override
    public void onItemClicked(DutyLog value) {}

    @Override
    public void onEditClicked(DutyLog value) {
        fireEvent(new DutyLogEvents.Edit(value.getId()));
    }

    @Override
    public void onFilterChanged() {
        this.page = 0;
        requestData( this.page );
    }

    @Override
    public void onResetFilterClicked() {
        filterView.resetFilter();
    }

    @Override
    public void onPageSelected( int page ) {
        this.page = page;
        requestData( this.page );
    }

    private void fillQuery() {
        query.setTypes(filterView.type().getValue());
        query.setDateRange(toDateRange(filterView.date().getValue()));
        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setSortField(filterView.sortField().getValue());

        Set<PersonShortView> employees = filterView.employees().getValue();
        query.setPersonIds(CollectionUtils.isEmpty(employees) ? null : employees.stream().map(PersonShortView::getId).collect(Collectors.toSet()));
    }

    private void requestData(int page) {
        view.clearRecords();
        fillQuery();

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
    private AbstractDutyLogFilterView filterView;

    @Inject
    private PolicyService policyService;
    @Inject
    private DutyLogControllerAsync dutyLogController;

    private AppEvents.InitDetails init;

    private int page;
    private DutyLogQuery query = new DutyLogQuery();
}
