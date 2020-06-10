package ru.protei.portal.ui.plan.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.query.PlanQuery;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PlanControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.plan.client.activity.filter.AbstractPlanFilterActivity;
import ru.protei.portal.ui.plan.client.activity.filter.AbstractPlanFilterView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public abstract class PlanTableActivity implements AbstractPlanTableActivity, AbstractPagerActivity, AbstractPlanFilterActivity, Activity {

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();
        view.setActivity( this );
        view.setAnimation( animation );
        pagerView.setActivity(this);
        filterView.setActivity( this );
        view.getFilterContainer().add( filterView.asWidget() );
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow( PlanEvents.ShowPlans event ) {
        initDetails.parent.clear();

        if (!policyService.hasPrivilegeFor(En_Privilege.PLAN_VIEW)) {
            fireEvent(new ForbiddenEvents.Show(initDetails.parent));
            return;
        }

        initDetails.parent.add( view.asWidget() );
        view.getPagerContainer().add(pagerView.asWidget());
        fireEvent(new ActionBarEvents.Clear());

        if (policyService.hasPrivilegeFor(En_Privilege.PLAN_CREATE)) {
            fireEvent(new ActionBarEvents.Add( CREATE_ACTION , null, UiConstants.ActionBarIdentity.PLAN_CREATE ));
        }

        this.preScroll = event.preScroll;

        loadTable();
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.PLAN_CREATE.equals(event.identity)) {
            return;
        }

        view.clearSelection();
        fireEvent(new PlanEvents.Edit());
    }


    @Override
    public void onItemClicked(Plan value) {
        persistScroll();
        showPreview(value);
    }

    @Override
    public void onEditClicked( Plan value ) {
        //проверить права
        persistScroll();
        fireEvent( new PlanEvents.Edit ( value.getId() ));
    }

    @Override
    public void onRemoveClicked(Plan value) {

    }

    @Override
    public void onFilterChanged() {
        loadTable();
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<Plan>> asyncCallback) {
        boolean isFirstChunk = offset == 0;
        PlanQuery query = makeQuery();
        query.setOffset(offset);
        query.setLimit(limit);

        planService.getPlanList(query, new FluentCallback<SearchResult<Plan>>()
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
                        restoreScroll();
                    }
                }));
    }

    private PlanQuery makeQuery() {
        PlanQuery query = new PlanQuery();
        query.setSearchString(filterView.search().getValue());
        query.setCreatorId(filterView.creator().getValue() == null ? null : filterView.creator().getValue().getId());
        query.setCreatedFrom(filterView.creationRange().getValue().from);
        query.setCreatedTo(filterView.creationRange().getValue().to);
        query.setStartDateFrom(filterView.startRange().getValue().from);
        query.setStartDateTo(filterView.startRange().getValue().to);
        query.setFinishDateFrom(filterView.finishRange().getValue().from);
        query.setFinishDateTo(filterView.finishRange().getValue().to);
        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setSortField(filterView.sortField().getValue());
        return query;
    }

    @Override
    public void onPageChanged(int page) {

    }

    @Override
    public void onPageSelected(int page) {
    }

    private void loadTable() {
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
    }

    private void persistScroll() {
        scrollTo = Window.getScrollTop();
    }

    private void showPreview (Plan value ) {
        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new PlanEvents.ShowPreview(view.getPreviewContainer(), value.getId()));
        }
    }

    private void restoreScroll() {
        if (!preScroll) {
            view.clearSelection();
            return;
        }

        Window.scrollTo(0, scrollTo);
        preScroll = false;
        scrollTo = 0;
    }

    @Inject
    Lang lang;
    @Inject
    AbstractPagerView pagerView;
    @Inject
    AbstractPlanTableView view;
    @Inject
    TableAnimation animation;
    @Inject
    PolicyService policyService;
    @Inject
    PlanControllerAsync planService;
    @Inject
    AbstractPlanFilterView filterView;

    private static String CREATE_ACTION;
    private Integer scrollTo = 0;
    private Boolean preScroll;
    private AppEvents.InitDetails initDetails;


}
