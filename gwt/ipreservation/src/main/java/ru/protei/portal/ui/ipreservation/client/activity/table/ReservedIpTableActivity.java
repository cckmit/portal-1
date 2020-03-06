package ru.protei.portal.ui.ipreservation.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.query.AccountQuery;
import ru.protei.portal.core.model.query.ReservedIpQuery;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IpReservationControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.ipreservation.client.activity.filter.AbstractIpReservationFilterActivity;
import ru.protei.portal.ui.ipreservation.client.activity.filter.AbstractIpReservationFilterView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Date;
import java.util.List;

import static ru.protei.portal.ui.common.client.util.PaginationUtils.PAGE_SIZE;
import static ru.protei.portal.ui.common.client.util.PaginationUtils.getTotalPages;

/**
 * Активность таблицы зарезервированных IP
 */
public abstract class ReservedIpTableActivity
        implements AbstractReservedIpTableActivity, AbstractIpReservationFilterActivity, AbstractPagerActivity, Activity
{

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();

        view.setActivity( this );
        view.setAnimation( animation );

        filterView.setActivity( this );
        view.getFilterContainer().add( filterView.asWidget() );

        pagerView.setActivity( this );
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event(Type.FILL_CONTENT)
    public void onShow( IpReservationEvents.Show event ) {
        if (!policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );
        view.getPagerContainer().add( pagerView.asWidget() );

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.RESERVED_IP_CREATE ) ?
            new ActionBarEvents.Add( CREATE_ACTION, null, UiConstants.ActionBarIdentity.RESERVED_IP ) :
            new ActionBarEvents.Clear()
        );

        clearScroll( event );

        requestReservedIps( this.page );
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.RESERVED_IP.equals(event.identity)) {
            return;
        }

        view.clearSelection();

        fireEvent(new IpReservationEvents.CreateReservedIp());
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

/*    @Event
    public void onConfirmRemove( ConfirmDialogEvents.Confirm event ) {
        if ( !event.identity.equals( getClass().getName() ) ) {
            return;
        }
        ipReservationService.removeReservedIp( reservedIpId, new RequestCallback< Boolean >() {
            @Override
            public void onError( Throwable throwable ) {}

            @Override
            public void onSuccess( Boolean aBoolean ) {
                fireEvent( new AccountEvents.Show() );
                fireEvent( new NotifyEvents.Show( lang.reservedIpRemoveSuccessed(), NotifyEvents.NotifyType.SUCCESS ) );
                reservedIpId = null;
            }
        } );
    }*/

    @Event
    public void onCancelRemove( ConfirmDialogEvents.Cancel event ) {
        //reservedIpId = null;
    }

    @Override
    public void onItemClicked(ReservedIp value) {
        showPreview(value);
    }

    @Override
    public void onEditClicked( ReservedIp value ) {
        persistScrollTopPosition();
        fireEvent(new IpReservationEvents.EditReservedIp(value));
    }

    @Override
    public void onRemoveClicked( UserLogin value ) {
        if (!policyService.hasPrivilegeFor(En_Privilege.PROJECT_REMOVE)) {
            return;
        }

        if ( value != null ) {
            reservedIpId = value.getId();
            fireEvent( new ConfirmDialogEvents.Show( getClass().getName(), lang.ReservedIpRemoveConfirmMessage() ) );
        }
    }

    @Override
    public void onFilterChanged() {
        this.page = 0;
        requestReservedIps( this.page );
    }

    @Override
    public void onPageSelected( int page ) {
        this.page = page;
        requestReservedIps( this.page );
    }

    private void requestReservedIps( int page ) {
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
    }

    private void showPreview(ReservedIp value) {
        if (value == null || value.getId() == null) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new IpReservationEvents.ShowPreview(view.getPreviewContainer(), value.getId(), true));
        }
    }

    private ReservedIpQuery makeQuery() {
        ReservedIpQuery query = new ReservedIpQuery();
        query.searchString = filterView.search().getValue();
        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setSortField(filterView.sortField().getValue());
        return query;
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

    private void clearScroll(IpReservationEvents.Show event) {
        if (event.clearScroll) {
            event.clearScroll = false;
            this.scrollTop = null;
            this.page = 0;
        }
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
    @Inject
    AbstractPagerView pagerView;
    @Inject
    PolicyService policyService;

    private Long accountId;

    private static String CREATE_ACTION;
    private AppEvents.InitDetails initDetails;

    private long marker;
    private Integer scrollTop;
    private int page = 0;
}
