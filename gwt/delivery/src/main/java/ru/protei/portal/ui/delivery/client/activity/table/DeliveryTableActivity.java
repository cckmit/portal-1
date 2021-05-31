package ru.protei.portal.ui.delivery.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DeliveryControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public abstract class DeliveryTableActivity implements AbstractDeliveryTableActivity,
        AbstractPagerActivity, Activity {

    @PostConstruct
    public void init() {
        CREATE_ACTION = lang.buttonCreate();

        view.setActivity(this);
        view.setAnimation(animation);

        pagerView.setActivity(this);
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        view.getFilterWidget().resetFilter();
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.initDetails = event;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(DeliveryEvents.Show event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.DELIVERY_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        view.getPagerContainer().add(pagerView.asWidget());

        if (policyService.hasPrivilegeFor(En_Privilege.DELIVERY_CREATE)){
            fireEvent( new ActionBarEvents.Add(CREATE_ACTION, null, UiConstants.ActionBarIdentity.DELIVERY));
        }
        this.preScroll = event.preScroll;

        loadTable();
    }

    @Event
    public void onChangeRow( DeliveryEvents.ChangeDelivery event ) {
        deliveryService.getDelivery(event.id, new FluentCallback<Delivery>()
                .withSuccess(delivery -> view.updateRow(delivery))
        );
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.DELIVERY.equals(event.identity)) {
            return;
        }

        view.clearSelection();

        fireEvent(new DeliveryEvents.Create());
    }

    @Override
    public void onFilterChanged() {
        loadTable();
    }

    @Override
    public void onItemClicked(Delivery value) {
        persistScroll();
        showPreview(value);
    }

    @Override
    public void onEditClicked(Delivery value) {
        persistScroll();
        fireEvent(new DeliveryEvents.Edit(value.getId()));
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<Delivery>> asyncCallback) {
        boolean isFirstChunk = offset == 0;

        DeliveryQuery query = getQuery();
        query.setOffset(offset);
        query.setLimit(limit);

        deliveryService.getDeliveries(query, new FluentCallback<SearchResult<Delivery>>()
                .withError(throwable -> {
                    errorHandler.accept(throwable);
                    asyncCallback.onFailure(throwable);
                })
                .withSuccess(sr -> {
                    if (!query.equals(getQuery())) {
                        loadData(offset, limit, asyncCallback);
                    }
                    if (isFirstChunk) {
                        view.setTotalRecords(sr.getTotalCount());
                        pagerView.setTotalPages(view.getPageCount());
                        pagerView.setTotalCount(sr.getTotalCount());
                        restoreScroll();
                    }

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

    private void persistScroll() {
        scrollTo = Window.getScrollTop();
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

    private void showPreview(Delivery value) {
        if (value == null || value.getId() == null) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new DeliveryEvents.ShowPreview(view.getPreviewContainer(), value.getId()));
        }
    }

    private DeliveryQuery getQuery() {
        return view.getFilterWidget().getFilterParamView().getQuery();
    }

    private void loadTable() {
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
    }

    @Inject
    private Lang lang;
    @Inject
    private TableAnimation animation;
    @Inject
    private PolicyService policyService;
    @Inject
    private AbstractDeliveryTableView view;
    @Inject
    private DeliveryControllerAsync deliveryService;
    @Inject
    private DefaultErrorHandler errorHandler;
    @Inject
    private AbstractPagerView pagerView;

    private static String CREATE_ACTION;
    private AppEvents.InitDetails initDetails;
    private Integer scrollTo = 0;
    private Boolean preScroll = false;
}
