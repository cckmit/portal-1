package ru.protei.portal.ui.plan.client.activity.table;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
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
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public abstract class PlanTableActivity implements AbstractPlanTableActivity, AbstractPagerActivity, Activity {

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();

        view.setActivity( this );
        view.setAnimation( animation );

        pagerView.setActivity(this);
      /*  filterView.setActivity( this );
        view.getFilterContainer().add( filterView.asWidget() );*/
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        /*filterView.resetFilter();*/
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
       /* view.getFilterContainer().add( filterView.asWidget() );*/

        fireEvent(new ActionBarEvents.Clear());

        if (policyService.hasPrivilegeFor(En_Privilege.PLAN_CREATE)) {
            fireEvent(new ActionBarEvents.Add( CREATE_ACTION , null, UiConstants.ActionBarIdentity.PLAN_CREATE ));
        }

        loadTable();
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.PLAN_CREATE.equals(event.identity)) {
            return;
        }

        view.clearSelection();

        animation.showDetails();
        fireEvent(new PlanEvents.CreatePlan(view.getPreviewContainer()));
    }


    @Override
    public void onItemClicked(Plan value) {
        onEditClicked(value);
    }

    @Override
    public void onEditClicked( Plan value ) {

    }

    @Override
    public void onRemoveClicked(Plan value) {

    }


    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<Plan>> asyncCallback) {
        boolean isFirstChunk = offset == 0;
        //с фильтра сформировать
        PlanQuery query = new PlanQuery();
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
                    }
                }));
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

    private static String CREATE_ACTION;
    private Integer scrollTop;
    private AppEvents.InitDetails initDetails;


}
