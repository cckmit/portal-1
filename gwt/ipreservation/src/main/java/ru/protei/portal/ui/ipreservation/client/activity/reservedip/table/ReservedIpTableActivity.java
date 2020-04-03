package ru.protei.portal.ui.ipreservation.client.activity.reservedip.table;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.ReservedIpQuery;
import ru.protei.portal.core.model.view.SubnetOption;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IpReservationControllerAsync;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.filter.AbstractReservedIpFilterActivity;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.filter.AbstractReservedIpFilterView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.stream.Collectors;

/**
 * Активность таблицы зарезервированных IP
 */
public abstract class ReservedIpTableActivity
        implements AbstractReservedIpTableActivity, AbstractReservedIpFilterActivity, Activity
{

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();

        view.setActivity( this );
        view.setAnimation( animation );

        filterView.setActivity( this );
        view.getFilterContainer().add( filterView.asWidget() );
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onShow( IpReservationEvents.ShowReservedIp event ) {
        if (!policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );
        view.getFilterContainer().clear();
        view.getFilterContainer().add( filterView.asWidget() );

        fireEvent(new ActionBarEvents.Clear());

        if (policyService.hasPrivilegeFor(En_Privilege.SUBNET_VIEW)) {
            fireEvent(new ActionBarEvents.Add(lang.reservedIpSubnetsBtn(), "", UiConstants.ActionBarIdentity.SUBNET));
        }

        if (policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_CREATE)) {
            fireEvent(new ActionBarEvents.Add( CREATE_ACTION , null, UiConstants.ActionBarIdentity.RESERVED_IP_CREATE ));
        }

        requestReservedIps();
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
        fireEvent(new ActionBarEvents.Clear());

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

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onChanged(IpReservationEvents.ChangedReservedIp event) {
        if ( event.needRefreshList ) {
            if (!CollectionUtils.isEmpty(event.reservedIpList)) {
                event.reservedIpList.forEach( ip -> updateListAndSelect(ip));
            } else {
                updateListAndSelect(event.reservedIp);
            }
            return;
        }

        view.updateRow(event.reservedIp);
    }

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
            fireEvent( new IpReservationEvents.EditReservedIp( view.getPreviewContainer(), value ) );
        }
    }

    @Override
    public void onEditClicked( ReservedIp value ) {
        onItemClicked(value);
    }

    @Override
    public void onRemoveClicked(ReservedIp value) {
        if (value == null || !policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_REMOVE)) {
            return;
        }

        fireEvent(new ConfirmDialogEvents.Show(lang.reservedIpReleaseConfirmMessage(), lang.reservedIpIpRelease(), onConfirmRemoveClicked(value)));
    }

    @Override
    public void onRefreshClicked(ReservedIp value) {
        if (value == null || !policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_VIEW)) {
            return;
        }

        refreshAction(value);
    }

    private Runnable onConfirmRemoveClicked(ReservedIp value) {
        return () -> ipReservationService.removeReservedIp(value, new FluentCallback<Long>()
                .withError(throwable -> {
                    if ((throwable instanceof RequestFailedException) && En_ResultStatus.UPDATE_OR_REMOVE_LINKED_OBJECT_ERROR.equals(((RequestFailedException) throwable).status)) {
                        fireEvent(new NotifyEvents.Show(lang.reservedIpUnableToRemove(), NotifyEvents.NotifyType.ERROR));
                    } else {
                        errorHandler.accept(throwable);
                    }
                })
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.reservedIpIpReleased(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IpReservationEvents.ShowReservedIp());
                })
        );
    }

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

    private static String CREATE_ACTION;
    private AppEvents.InitDetails initDetails;
}
