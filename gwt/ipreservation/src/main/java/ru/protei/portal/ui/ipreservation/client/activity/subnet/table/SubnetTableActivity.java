package ru.protei.portal.ui.ipreservation.client.activity.subnet.table;

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
import ru.protei.portal.ui.ipreservation.client.activity.subnet.filter.AbstractSubnetFilterActivity;
import ru.protei.portal.ui.ipreservation.client.activity.subnet.filter.AbstractSubnetFilterView;
import ru.protei.winter.core.utils.beans.SearchResult;

/**
 * Активность таблицы подсетей
 */
public abstract class SubnetTableActivity
        implements AbstractSubnetTableActivity, AbstractSubnetFilterActivity, Activity
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
    public void onShow( IpReservationEvents.ShowSubnet event ) {
        if (!policyService.hasPrivilegeFor(En_Privilege.SUBNET_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );
        view.getFilterContainer().add( filterView.asWidget() );

        fireEvent(new ActionBarEvents.Clear());

        if (policyService.hasPrivilegeFor( En_Privilege.SUBNET_CREATE )) {
            fireEvent(new ActionBarEvents.Add(CREATE_ACTION, null, UiConstants.ActionBarIdentity.SUBNET_CREATE));
        }

        requestSubnets();
    }

    @Event
    public void onCloseEdit(IpReservationEvents.CloseEdit event) {
        animation.closeDetails();
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.SUBNET_CREATE.equals(event.identity)) {
            return;
        }

        view.clearSelection();

        animation.showDetails();
        fireEvent(new IpReservationEvents.EditSubnet(view.getPreviewContainer()));
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onChanged(IpReservationEvents.ChangedSubnet event) {
        if ( event.needRefreshList ) {
            updateListAndSelect(event.subnet);
            return;
        }

        view.updateRow(event.subnet);
    }

    @Override
    public void onFilterChanged() {
        requestSubnets();
    }

    @Override
    public void onItemClicked(Subnet value) {
        onEditClicked(value);
    }

    @Override
    public void onEditClicked( Subnet value ) {
        if ( !policyService.hasPrivilegeFor( En_Privilege.SUBNET_EDIT ) ) {
            return;
        }

        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent( new IpReservationEvents.EditSubnet( view.getPreviewContainer(), value ) );
        }
    }

    @Override
    public void onRemoveClicked(Subnet value) {
        if (value == null || !policyService.hasPrivilegeFor(En_Privilege.SUBNET_REMOVE)) {
            return;
        }

        ipReservationService.isSubnetAvailableToRemove(
                value.getId(),
                new FluentCallback<Boolean>()
                        .withError(throwable -> showErrorFromServer(throwable))
                        .withSuccess(result -> {
                                    String confMessage = result ?
                                            lang.reservedIpSubnetRemoveConfirmMessage() :
                                            lang.reservedIpSubnetRemoveWithIpsConfirmMessage();

                                    fireEvent(new ConfirmDialogEvents.Show(confMessage, lang.reservedIpSubnetRemove(), onConfirmRemoveClicked(value)));
                                }
                                )
        );
    }

    @Override
    public void onRefreshClicked(Subnet value) {
        if (value != null && policyService.hasPrivilegeFor(En_Privilege.SUBNET_VIEW)) {
            refreshAction(value);
        }
    }

    private Runnable onConfirmRemoveClicked(Subnet value) {
        return () -> ipReservationService.removeSubnet(value, true, new FluentCallback<Long>()
                .withError(throwable -> showError(lang.reservedIpSubnetUnableToRemove()))
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.reservedIpSubnetRemoved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IpReservationEvents.ChangeModel());
                    fireEvent(new IpReservationEvents.ShowSubnet());
                })
        );
    }

    private void updateListAndSelect(Subnet subnet) {
        requestSubnets();
        onItemClicked( subnet );
    }

    private void requestSubnets() {
        view.clearRecords();
        animation.closeDetails();

        ipReservationService.getSubnetList( makeQuery(), new FluentCallback<SearchResult<Subnet>>()
                .withError(throwable -> showError(lang.errGetList()))
                .withSuccess(sr -> {
                    view.clearRecords();
                    sr.getResults().forEach(view::addRow);
                }));
    }

    private ReservedIpQuery makeQuery() {
        ReservedIpQuery query = new ReservedIpQuery();
        query.searchString = filterView.search().getValue();
        query.setSortField(filterView.sortField().getValue());
        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        return query;
    }

    private void refreshAction(Subnet subnet) {
/*        return () -> ipReservationService.refreshSubnet(subnet, new FluentCallback<Long>()
                .withSuccess(id -> {
                    fireEvent(new IpReservationEvents.ShowSubnet());
                }));*/
/*        fireEvent(new NotifyEvents.Show(lang.refresh(), NotifyEvents.NotifyType.SUCCESS));*/
        Window.alert("Refresh subnet " + subnet.getAddress() + " under construction :(");
    }

    private void showErrorFromServer(Throwable throwable) {
        errorHandler.accept(throwable);
    }

    private void showError(String error) {
        fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }

    @Inject
    Lang lang;
    @Inject
    AbstractSubnetTableView view;
    @Inject
    AbstractSubnetFilterView filterView;
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
