package ru.protei.portal.ui.ipreservation.client.activity.reservedip.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.ReservedIpQuery;
import ru.protei.portal.core.model.view.SubnetOption;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IpReservationControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.filter.AbstractReservedIpFilterActivity;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.filter.AbstractReservedIpFilterView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Активность таблицы зарезервированных IP
 */
public abstract class ReservedIpTableActivity
        implements AbstractReservedIpTableActivity, AbstractReservedIpFilterActivity,
        AbstractPagerActivity, Activity {

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
    public void onShow( IpReservationEvents.ShowReservedIp event ) {
        if (!policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );
        view.getPagerContainer().add(pagerView.asWidget());
        view.getFilterContainer().add( filterView.asWidget() );

        fireEvent(new ActionBarEvents.Clear());

        if (policyService.hasPrivilegeFor(En_Privilege.SUBNET_VIEW)) {
            fireEvent(new ActionBarEvents.Add(lang.reservedIpSubnetsBtn(), "", UiConstants.ActionBarIdentity.SUBNET));
        }

        if (policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_CREATE)) {
            fireEvent(new ActionBarEvents.Add( CREATE_ACTION , null, UiConstants.ActionBarIdentity.RESERVED_IP_CREATE ));
        }

        clearScroll(event);

        loadTable();
    }

    @Event
    public void onCloseEdit(IpReservationEvents.CloseEdit event) {
        animation.closeDetails();
    }

    @Event
    public void onSubnetBtnClicked(ActionBarEvents.Clicked event) {
        if (!(UiConstants.ActionBarIdentity.SUBNET.equals(event.identity))) {
            return;
        }

        if (!policyService.hasPrivilegeFor(En_Privilege.SUBNET_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        fireEvent(new ActionBarEvents.Clear());
        fireEvent(new IpReservationEvents.ShowSubnet());
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.RESERVED_IP_CREATE.equals(event.identity)) {
            return;
        }

        view.clearSelection();

        animation.showDetails();
        fireEvent(new IpReservationEvents.CreateReservedIp(view.getPreviewContainer()));
    }

    @Override
    public void onFilterChanged() {
        loadTable();
    }

    @Override
    public void onItemClicked(ReservedIp value) {
        onEditClicked(value);
    }

    @Override
    public void onEditClicked( ReservedIp value ) {
        if ( !hasEditPrivileges(value == null ? null : value.getOwnerId())) {
            return;
        }

        persistScrollTopPosition();

        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent( new IpReservationEvents.EditReservedIp( view.getPreviewContainer(), value ) );
        }
    }

    @Override
    public void onRemoveClicked(ReservedIp value) {
        if (value != null && hasRemovePrivileges(value.getOwnerId())) {
            fireEvent(new ConfirmDialogEvents.Show(lang.reservedIpReleaseConfirmMessage(), lang.reservedIpIpRelease(), onConfirmRemoveClicked(value)));
        }
    }

    @Override
    public void onRefreshClicked(ReservedIp value) {
        if (value != null && policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_VIEW)) {
            refreshAction(value);
        }
    }

    private Runnable onConfirmRemoveClicked(ReservedIp value) {
        return () -> ipReservationService.removeReservedIp(value, new FluentCallback<Long>()
                .withError(throwable -> showError(lang.reservedIpUnableToRemove()))
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.reservedIpIpReleased(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IpReservationEvents.ShowReservedIp());
                })
        );
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<ReservedIp>> asyncCallback) {
        boolean isFirstChunk = offset == 0;
        ReservedIpQuery query = makeQuery();
        query.setOffset(offset);
        query.setLimit(limit);

        ipReservationService.getReservedIpList(query, new FluentCallback<SearchResult<ReservedIp>>()
                .withError(throwable -> {
                    showError(lang.errGetList());
                    asyncCallback.onFailure(throwable);
                })
                .withSuccess(sr -> {
                    asyncCallback.onSuccess(sr.getResults());
                    if (isFirstChunk) {
                        view.setTotalRecords(sr.getTotalCount());
                        pagerView.setTotalPages(view.getPageCount());
                        pagerView.setTotalCount(sr.getTotalCount());
                        restoreScrollTopPositionOrClearSelection();
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
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
    }

    private ReservedIpQuery makeQuery() {
        ReservedIpQuery query = new ReservedIpQuery();
        query.setSearchString(filterView.search().getValue());
        query.setOwnerId(filterView.owner().getValue() == null ? null : filterView.owner().getValue().getId());
        query.setSubnetIds(filterView.subnets().getValue() == null
                ? null
                : filterView.subnets().getValue().stream()
                .map(SubnetOption::getId)
                .collect(Collectors.toList())
        );
        query.setReservedFrom(filterView.reserveRange().getValue().from);
        query.setReservedTo(filterView.reserveRange().getValue().to);
        query.setReleasedFrom(filterView.releaseRange().getValue().from);
        query.setReleasedTo(filterView.releaseRange().getValue().to);
        query.setLastActiveFrom(filterView.lastActiveRange().getValue().from);
        query.setLastActiveTo(filterView.lastActiveRange().getValue().to);
        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setSortField(filterView.sortField().getValue());
        return query;
    }

    private void refreshAction(ReservedIp reservedIp) {
/*        return () -> ipReservationService.refreshReservedIp(reservedIp, new FluentCallback<Long>()
                .withSuccess(id -> {
                    fireEvent(new IpReservationEvents.Show());
                }));*/
        //fireEvent(new NotifyEvents.Show(lang.refresh(), NotifyEvents.NotifyType.SUCCESS));
        Window.alert("Refresh IP " + reservedIp.getIpAddress());
    }

    private void persistScrollTopPosition() {
        scrollTop = Window.getScrollTop();
    }

    private void restoreScrollTopPositionOrClearSelection() {
        if (scrollTop == null) {
            view.clearSelection();
            return;
        }
        int trh = RootPanel.get(DebugIds.DEBUG_ID_PREFIX + DebugIds.APP_VIEW.GLOBAL_CONTAINER).getOffsetHeight() - Window.getClientHeight();
        if (scrollTop <= trh) {
            Window.scrollTo(0, scrollTop);
            scrollTop = null;
        }
    }

    private void clearScroll(IpReservationEvents.ShowReservedIp event) {
        if (event.clearScroll) {
            event.clearScroll = false;
            this.scrollTop = null;
        }
    }

    @Override
    public boolean hasEditPrivileges(Long ownerId) {
        boolean isAdmin = policyService.hasSystemScopeForPrivilege(En_Privilege.RESERVED_IP_EDIT);
        boolean isUserWithAccess = policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_EDIT)
                && Objects.equals(ownerId, policyService.getProfile().getId());
        return isAdmin || isUserWithAccess;
    }

    @Override
    public boolean hasRemovePrivileges(Long ownerId) {
        boolean isAdmin = policyService.hasSystemScopeForPrivilege(En_Privilege.RESERVED_IP_REMOVE);
        boolean isUserWithAccess = policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_REMOVE)
                && Objects.equals(ownerId, policyService.getProfile().getId());
        return isAdmin || isUserWithAccess;
    }

    @Override
    public boolean hasRefreshPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_VIEW);
    }

    private void showError(String error) {
        fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }

    @Inject
    Lang lang;
    @Inject
    AbstractReservedIpTableView view;
    @Inject
    AbstractReservedIpFilterView filterView;
    @Inject
    IpReservationControllerAsync ipReservationService;
    @Inject
    TableAnimation animation;
    @Inject
    PolicyService policyService;
    @Inject
    DefaultErrorHandler errorHandler;
    @Inject
    AbstractPagerView pagerView;

    private static String CREATE_ACTION;
    private Integer scrollTop;
    private AppEvents.InitDetails initDetails;
}
