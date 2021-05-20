package ru.protei.portal.ui.delivery.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.DeliveryFilterDto;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.ui.common.client.activity.deliveryfilter.AbstractDeliveryCollapseFilterActivity;
import ru.protei.portal.ui.common.client.activity.deliveryfilter.AbstractDeliveryCollapseFilterView;
import ru.protei.portal.ui.common.client.activity.deliveryfilter.AbstractDeliveryFilterModel;
import ru.protei.portal.ui.common.client.activity.deliveryfilter.DeliveryFilterService;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DeliveryControllerAsync;
import ru.protei.portal.ui.common.client.widget.deliveryfilter.DeliveryFilterWidget;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public abstract class DeliveryTableActivity implements AbstractDeliveryTableActivity,
        AbstractDeliveryCollapseFilterActivity, AbstractDeliveryFilterModel, AbstractPagerActivity, Activity {

    @PostConstruct
    public void init() {
        view.setActivity(this);
        view.setAnimation(animation);
        pagerView.setActivity(this);

        filterView.getDeliveryFilterParams().setModel(this);

        collapseFilterView.setActivity(this);
        collapseFilterView.getContainer().add(filterView.asWidget());
        view.getFilterContainer().add( collapseFilterView.asWidget() );

        toggleFilterCollapseState();
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter(DEFAULT_MODIFIED_RANGE);
        filterView.presetFilterType();
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

        applyFilterViewPrivileges();

        init.parent.clear();
        init.parent.add(view.asWidget());
        view.getPagerContainer().add(pagerView.asWidget());

        filterView.showUserFilterControls();

        fireEvent(policyService.hasPrivilegeFor(En_Privilege.DELIVERY_CREATE) ?
                new ActionBarEvents.Add(lang.buttonCreate(), null, UiConstants.ActionBarIdentity.DELIVERY) :
                new ActionBarEvents.Clear()
        );

        if(!policyService.hasSystemScopeForPrivilege(En_Privilege.DELIVERY_VIEW)){
            if (policyService.isSubcontractorCompany()) {
                filterView.getDeliveryFilterParams().presetManagerCompany(policyService.getProfile().getCompany());
            } else {
                filterView.getDeliveryFilterParams().presetCompany(policyService.getProfile().getCompany());
            }
        }

        this.preScroll = event.preScroll;

        if (event.deliveryFilterDto == null || event.deliveryFilterDto.getQuery() == null) {
            loadTable();
        } else {
            fillFilterFieldsByQuery(event.deliveryFilterDto);
        }

        validateDepartureRange(filterView.getDeliveryFilterParams().isDepartureRangeValid());
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
    public void onFilterCollapse() {
        animation.filterCollapse();
        filterService.setFilterCollapsed(true);
    }

    @Override
    public void onFilterRestore() {
        animation.filterRestore();
        filterService.setFilterCollapsed(false);
    }

    @Override
    public void onUserFilterChanged() {
        String validateString = filterView.getDeliveryFilterParams().validateMultiSelectorsTotalCount();
        if ( validateString != null ){
            fireEvent( new NotifyEvents.Show( lang.errTooMuchCompanies(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        boolean departureRangeValid = filterView.getDeliveryFilterParams().isDepartureRangeValid();

        if(departureRangeValid) {
            loadTable();
        }
        validateDepartureRange(departureRangeValid);
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

    private void validateDepartureRange(boolean isValid){
        filterView.getDeliveryFilterParams().setDepartureRangeValid(true, isValid);
        filterView.createEnabled().setEnabled(isValid);
    }

    private void loadTable() {
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
    }

    private void fillFilterFieldsByQuery(DeliveryFilterDto<DeliveryQuery> caseFilterDto) {
        filterView.resetFilter(DEFAULT_MODIFIED_RANGE);
        filterView.userFilter().setValue(caseFilterDto.getDeliveryFilter().toShortView());

        final DeliveryQuery deliveryQuery = caseFilterDto.getQuery();

        //TODO create DeliveryFilterService
//        filterService.getSelectorsParams(deliveryQuery, new RequestCallback<SelectorsParams>() {
//            @Override
//            public void onError(Throwable throwable) {
//                fireEvent(new NotifyEvents.Show(lang.errNotFound(), NotifyEvents.NotifyType.ERROR));
//            }
//
//            @Override
//            public void onSuccess(SelectorsParams selectorsParams) {
//                filterView.getDeliveryFilterParams().fillFilterFields(caseQuery, selectorsParams);
//            }
//        });
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
        return filterView.getDeliveryFilterParams().getFilterFields(En_DeliveryFilterType.DELIVERY_OBJECTS);
    }

    private void applyFilterViewPrivileges() {
        filterView.getDeliveryFilterParams().productsVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_FILTER_PRODUCT_VIEW ) );
        filterView.getDeliveryFilterParams().managersVisibility().setVisible(policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW) || policyService.isSubcontractorCompany());
    }

    private void toggleFilterCollapseState() {
        Boolean isCollapsed = filterService.isFilterCollapsed();
        if (isCollapsed == null) {
            return;
        }
        if (isCollapsed) {
            animation.filterCollapse();
        } else {
            animation.filterRestore();
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
    private DeliveryFilterWidget filterView;
    @Inject
    private AbstractDeliveryCollapseFilterView collapseFilterView;
    @Inject
    private DeliveryFilterService filterService;
    @Inject
    private DeliveryControllerAsync deliveryService;
    @Inject
    private DefaultErrorHandler errorHandler;
    @Inject
    private AbstractPagerView pagerView;

    private Integer scrollTo = 0;
    private Boolean preScroll = false;
    private AppEvents.InitDetails init;
    private static final DateIntervalWithType DEFAULT_MODIFIED_RANGE = new DateIntervalWithType(null, En_DateIntervalType.PREVIOUS_AND_THIS_MONTH);
}
