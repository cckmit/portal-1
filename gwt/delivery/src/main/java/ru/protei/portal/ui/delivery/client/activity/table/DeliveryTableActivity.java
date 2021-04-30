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
import ru.protei.portal.core.model.query.BaseQuery;
import ru.protei.portal.ui.common.client.activity.deliveryfilter.AbstractDeliveryFilterActivity;
import ru.protei.portal.ui.common.client.activity.deliveryfilter.AbstractDeliveryFilterView;
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
        AbstractDeliveryFilterActivity, AbstractPagerActivity, Activity {

    @PostConstruct
    public void init() {
        view.setActivity(this);
        view.setAnimation(animation);
        pagerView.setActivity(this);
        filterView.setActivity(this);
        view.getFilterContainer().add(filterView.asWidget());
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(DeliveryEvents.Show event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.DELIVERY_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(init.parent));
            return;
        }

        init.parent.clear();
        init.parent.add(view.asWidget());
        view.getPagerContainer().add(pagerView.asWidget());

        fireEvent(policyService.hasPrivilegeFor(En_Privilege.DELIVERY_CREATE) ?
                new ActionBarEvents.Add(lang.buttonCreate(), null, UiConstants.ActionBarIdentity.DELIVERY) :
                new ActionBarEvents.Clear()
        );

        this.preScroll = event.preScroll;

        loadTable();
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.DELIVERY.equals(event.identity)) {
            return;
        }

        view.clearSelection();

        fireEvent(new DeliveryEvents.Edit());
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
    public void onFilterChanged() {
        loadTable();
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<Delivery>> asyncCallback) {
        boolean isFirstChunk = offset == 0;
        BaseQuery query = makeQuery();
        query.setOffset(offset);
        query.setLimit(limit);
        deliveryService.getDeliveries(query, new FluentCallback<SearchResult<Delivery>>()
                .withError(throwable -> {
                    errorHandler.accept(throwable);
                    asyncCallback.onFailure(throwable);
                })
                .withSuccess(sr -> {
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

    private void loadTable() {
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
    }

    private BaseQuery makeQuery() {
        return new BaseQuery();
//        DeliveryQuery query = new DeliveryQuery();
//        query.searchString = filterView.searchString().getValue();
//        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
//        query.setSortField(filterView.sortField().getValue());
//        query.setContractorIds(collectIds(filterView.contractors().getValue()));
//        query.setCuratorIds(collectIds(filterView.curators().getValue()));
//        query.setOrganizationIds(collectIds(filterView.organizations().getValue()));
//        query.setManagerIds(collectIds(filterView.managers().getValue()));
//        query.setTypes(nullIfEmpty(listOfOrNull(filterView.types().getValue())));
//        query.setCaseTagsIds(nullIfEmpty(toList(filterView.tags().getValue(), caseTag -> caseTag == null ? CrmConstants.CaseTag.NOT_SPECIFIED : caseTag.getId())));
//        query.setStates(nullIfEmpty(listOfOrNull(filterView.states().getValue())));
//        ProductDirectionInfo value = filterView.direction().getValue();
//        query.setDirectionId(value == null ? null : value.id);
//        query.setKind(filterView.kind().getValue());
//        query.setDateSigningRange(toDateRange(filterView.dateSigningRange().getValue()));
//        query.setDateValidRange(toDateRange(filterView.dateValidRange().getValue()));
//        query.setDeliveryNumber(filterView.deliveryNumber().getValue());
//        return query;
    }

    private void showPreview(Delivery value) {
        if (value == null || value.getId() == null) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new DeliveryEvents.ShowPreview(view.getPreviewContainer(), value.getId()));
        }
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

    @Inject
    private Lang lang;
    @Inject
    private TableAnimation animation;
    @Inject
    private PolicyService policyService;
    @Inject
    private AbstractDeliveryTableView view;
    @Inject
    private AbstractDeliveryFilterView filterView;
    @Inject
    private DeliveryControllerAsync deliveryService;
    @Inject
    private DefaultErrorHandler errorHandler;
    @Inject
    private AbstractPagerView pagerView;

    private Integer scrollTo = 0;
    private Boolean preScroll = false;
    private AppEvents.InitDetails init;
}
