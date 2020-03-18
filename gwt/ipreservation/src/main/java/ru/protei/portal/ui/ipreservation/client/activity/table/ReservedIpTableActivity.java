package ru.protei.portal.ui.ipreservation.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.ReservedIpQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IpReservationControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.ipreservation.client.activity.filter.AbstractIpReservationFilterActivity;
import ru.protei.portal.ui.ipreservation.client.activity.filter.AbstractIpReservationFilterView;
import ru.protei.winter.core.utils.beans.SearchResult;

/**
 * Активность таблицы зарезервированных IP
 */
public abstract class ReservedIpTableActivity
        implements AbstractReservedIpTableActivity, AbstractIpReservationFilterActivity, /*AbstractPagerActivity,*/ Activity
{

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();

        view.setActivity( this );
        view.setAnimation( animation );

        filterView.setActivity( this );
        view.getFilterContainer().add( filterView.asWidget() );

        //pagerView.setActivity( this );
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        //filterView.resetFilter();
    }

    //@Event(Type.FILL_CONTENT)
    @Event
    public void onShow( IpReservationEvents.Show event ) {
        if (!policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );
        //view.getPagerContainer().add( pagerView.asWidget() );

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.RESERVED_IP_CREATE ) ?
            new ActionBarEvents.Add( CREATE_ACTION, null, UiConstants.ActionBarIdentity.RESERVED_IP ) :
            new ActionBarEvents.Clear()
        );

/*        clearScroll( event );*/

        requestReservedIps( /*this.page*/ );
    }

    @Event
    public void onClosePreview(DocumentTypeEvents.ClosePreview event) {
        animation.closeDetails();
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.RESERVED_IP.equals(event.identity)) {
            return;
        }

        view.clearSelection();

        animation.showDetails();
        fireEvent(new IpReservationEvents.CreateReservedIp());
        //fireEvent(new IpReservationEvents.ShowPreview(view.getPreviewContainer(), null));
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onChanged(IpReservationEvents.ChangedReservedIp event) {
        if ( event.needRefreshList ) {
            updateListAndSelect(event.reservedIp);
            return;
        }

        view.updateRow(event.reservedIp);
    }

/*    @Override
    public void onItemClicked(Subnet value) {
        if ( !policyService.hasPrivilegeFor( En_Privilege.SUBNET_EDIT ) ) {
            return;
        }

        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent( new IpReservationEvents.EditSubnet( value ) );
        }
    }*/

    @Override
    public void onFilterChanged() {
        requestReservedIps();
    }

    @Override
    public void onItemClicked(ReservedIp value) {
        if ( !policyService.hasPrivilegeFor( En_Privilege.RESERVED_IP_EDIT ) ) {
            return;
        }

        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent( new IpReservationEvents.EditReservedIp( view.getPreviewContainer(), value.getId() ) );
        }
    }

/*    @Override
    public void onEditClicked( Subnet value ) {
*//*        persistScrollTopPosition();*//*
        onItemClicked(value);
    }*/

    @Override
    public void onEditClicked( ReservedIp value ) {
        /*        persistScrollTopPosition();*/
        onItemClicked(value);
    }

/*    @Override
    public void onRemoveClicked(Subnet value) {

        if (!policyService.hasPrivilegeFor(En_Privilege.SUBNET_REMOVE)) {
            return;
        }
        if (value != null) {
            fireEvent(new ConfirmDialogEvents.Show(lang.reservedIpSubnetRemoveConfirmMessage(), lang.reservedIpSubnetRemove(), removeAction(value)));
        }
    }*/

    @Override
    public void onRemoveClicked(ReservedIp value) {

        if (!policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_REMOVE)) {
            return;
        }
        if (value != null) {
            fireEvent(new ConfirmDialogEvents.Show(lang.reservedIpReleaseConfirmMessage(), lang.reservedIpIpRelease(), removeAction(value)));
        }
    }

    @Override
    public void onRefreshClicked(ReservedIp value) {

        if (!policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_VIEW)) {
            return;
        }
        if (value != null) {
            refreshAction(value);
/*            fireEvent(new ConfirmDialogEvents.Show(lang.reservedIpReleaseConfirmMessage(), lang.reservedIpIpRelease(), removeAction(value)));*/
        }
    }

/*    @Override
    public void onPageSelected( int page ) {
        this.page = page;
        requestReservedIps( this.page );
    }*/

    private void updateListAndSelect(ReservedIp reservedIp ) {
        requestReservedIps();
        onItemClicked( reservedIp );
    }

    private void requestReservedIps() {
        view.clearRecords();
        animation.closeDetails();

        ipReservationService.getReservedIpList( makeQuery(), new RequestCallback<SearchResult<ReservedIp>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess(SearchResult<ReservedIp> result) {
                view.clearRecords();
                result.getResults().forEach(view::addRow);
            }
        });
    }

/*    private void requestReservedIps( *//*int page*//* ) {
        view.clearRecords();
        animation.closeDetails();

        boolean isFirstChunk = page == 0;
        marker = new Date().getTime();

        ReservedIpQuery query = makeQuery();
        query.setOffset( page*PAGE_SIZE );
        query.setLimit( PAGE_SIZE );

        ipReservationService.getReservedIpList( query, new FluentCallback< SearchResult< ReservedIp > >()
                .withMarkedSuccess( marker, ( m, r ) -> {
                    if ( marker == m ) {
                        if ( isFirstChunk ) {
                            pagerView.setTotalCount( r.getTotalCount() );
                            pagerView.setTotalPages( getTotalPages( r.getTotalCount() ) );
                        }
                        pagerView.setCurrentPage( page );
                        r.getResults().forEach(view::addRow);
                        restoreScrollTopPositionOrClearSelection();
                    }
                } )
                .withErrorMessage( lang.errGetList() ) );
    }*/

/*    private void showPreview(ReservedIp value) {
        if (value == null || value.getId() == null) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new IpReservationEvents.ShowPreview(view.getPreviewContainer(), value.getId(), true));
        }
    }*/

    private ReservedIpQuery makeQuery() {
        ReservedIpQuery query = new ReservedIpQuery();
        query.searchString = filterView.search().getValue();
        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setSortField(filterView.sortField().getValue());
        return query;
    }

/*    private void persistScrollTopPosition() {
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
    }*/

/*    private void clearScroll(IpReservationEvents.Show event) {
        if (event.clearScroll) {
            event.clearScroll = false;
            this.scrollTop = null;
            this.page = 0;
        }
    }*/

    private Runnable removeAction(ReservedIp reservedIp) {
        return () -> ipReservationService.removeReservedIp(reservedIp, new FluentCallback<Long>()
                .withSuccess(id -> {
                    fireEvent(new IpReservationEvents.Show());
                    fireEvent(new NotifyEvents.Show(lang.reservedIpIpReleased(), NotifyEvents.NotifyType.SUCCESS));
                }));
    }

/*    private Runnable removeAction(Subnet subnet) {
        return () -> ipReservationService.removeSubnet(subnet, new FluentCallback<Long>()
                .withSuccess(id -> {
                    fireEvent(new DocumentEvents.Show());
                    fireEvent(new NotifyEvents.Show(lang.reservedIpSubnetRemoved(), NotifyEvents.NotifyType.SUCCESS));
                }));
    }*/

    private void refreshAction(ReservedIp reservedIp) {
/*        return () -> ipReservationService.refreshReservedIp(reservedIp, new FluentCallback<Long>()
                .withSuccess(id -> {
                    fireEvent(new IpReservationEvents.Show());
                }));*/
        //fireEvent(new NotifyEvents.Show(lang.refresh(), NotifyEvents.NotifyType.SUCCESS));
        Window.alert("Refresh IPs");
    }

    @Inject
    Lang lang;
    @Inject
    AbstractReservedIpTableView view;
    @Inject
    AbstractIpReservationFilterView filterView;
    @Inject
    IpReservationControllerAsync ipReservationService;
    @Inject
    TableAnimation animation;
/*    @Inject
    AbstractPagerView pagerView;*/
    @Inject
    PolicyService policyService;
    @Inject
    DefaultErrorHandler errorHandler;

    private static String CREATE_ACTION;
    private AppEvents.InitDetails initDetails;

/*    private long marker;
    private Integer scrollTop;
    private int page = 0;*/
}
