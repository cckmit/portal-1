package ru.protei.portal.ui.ipreservation.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.query.ReservedIpQuery;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IpReservationControllerAsync;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.ipreservation.client.activity.filter.AbstractIpReservationFilterView;
import ru.protei.portal.ui.project.client.activity.filter.AbstractProjectFilterActivity;
import ru.protei.portal.ui.project.client.activity.filter.AbstractProjectFilterView;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Активность таблицы зарезервированных IP
 */
public abstract class ReservedIpTableActivity
        implements AbstractReservedIpTableActivityTableActivity, AbstractReservedIpTableActivityFilterActivity, Activity
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
    public void onShow( IpReservationEvents.Show event ) {
        if (!policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.RESERVED_IP_CREATE ) ?
            new ActionBarEvents.Add( CREATE_ACTION, null, UiConstants.ActionBarIdentity.RESERVED_IP ) :
            new ActionBarEvents.Clear()
        );

        projectIdForRemove = null;

        clearScroll(event);

        requestProjects( null );
    }

    @Event
    public void onCreateClicked( ActionBarEvents.Clicked event ) {
        if ( !UiConstants.ActionBarIdentity.RESERVED_IP.equals( event.identity ) ) {
            return;
        }

        view.clearSelection();

        fireEvent(new IpReservationEvents.EditReservedIp());
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onConfirmRemove(ConfirmDialogEvents.Confirm event) {

        if (!getClass().getName().equals(event.identity)) {
            return;
        }

        if (!policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_REMOVE)) {
            return;
        }

        ipReservationService.removeReservedIp(projectIdForRemove, new FluentCallback<Boolean>()
                .withError(t -> {
                    projectIdForRemove = null;
                })
                .withSuccess(result -> {
                    projectIdForRemove = null;
                    fireEvent(new NotifyEvents.Show(lang.projectRemoveSucceeded(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new ProjectEvents.ChangeModel());
                    fireEvent(new ProjectEvents.Show());
                })
        );
    }

    @Event
    public void onProjectCancelRemove(ConfirmDialogEvents.Cancel event) {
        if (!getClass().getName().equals(event.identity)) {
            return;
        }
        projectIdForRemove = null;
    }

    @Event
    public void onChangeRow( ProjectEvents.ChangeProject event ) {
        regionService.getProject(event.id, new FluentCallback<Project>()
                .withSuccess(result -> {
                    view.updateRow(result);
                }));
    }

    @Override
    public void onItemClicked( Project value ) {
        showPreview( value );
    }

    @Override
    public void onEditClicked( Project value ) {
        persistScrollTopPosition();
        fireEvent(new ProjectEvents.Edit(value.getId()));
    }

    @Override
    public void onRemoveClicked(Project value) {
        if (!policyService.hasPrivilegeFor(En_Privilege.PROJECT_REMOVE)) {
            return;
        }

        if (value == null) {
            return;
        }

        projectIdForRemove = value.getId();
        fireEvent(new ConfirmDialogEvents.Show(getClass().getName(), lang.projectRemoveConfirmMessage(value.getName())));
    }

    @Override
    public void onFilterChanged() {
        requestProjects( null );
    }

    private void requestProjects( Project rowToSelect ) {
        if ( rowToSelect == null ) {
            view.clearRecords();
            animation.closeDetails();
        }

        ipReservationService.getReservedIpList( getQuery(), new RequestCallback<Map<String, List<ReservedIp>>>() {
                @Override
                public void onError( Throwable throwable ) {
                    fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
                }

                @Override
                public void onSuccess( Map<String, List<Project>> result ) {
                    fillRows( result );
                    if ( rowToSelect != null ) {
                        view.updateRow( rowToSelect );
                    }
                    restoreScrollTopPositionOrClearSelection();
                }
            } );
    }

    private void fillRows( Map<String, List<Project>> result ) {
        view.clearRecords();
        for ( Map.Entry<String, List<Project>> entry : result.entrySet() ) {
            view.addSeparator( entry.getKey() );

            for ( Project project : entry.getValue() ) {
                view.addRow(project);
            }
        }
    }

    private void showPreview ( Project value ) {
        currentValue = value;
        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent( new ProjectEvents.ShowPreview( view.getPreviewContainer(), value.getId() ) );
        }
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

    private ReservedIpQuery getQuery() {
        ReservedIpQuery query = new ReservedIpQuery();
        query.setSearchString(filterView.searchPattern().getValue());
        query.setOwnerIds(
                filterView.employee().getValue().stream()
                        .map( (emp)-> emp.id )
                        .collect( Collectors.toSet() )
        );
        query.setSubnetId(
                filterView.subnet().getValue() == null
                        ? null
                        : filterView.subnet().getValue().id
        );
        query.setSortField(filterView.sortField().getValue());
        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setOnlyLocalSubnets(filterView.onlyLocalSubnets().getValue());
        return query;
    }

    private void clearScroll(IpReservationEvents.Show event) {
        if (event.clearScroll) {
            event.clearScroll = false;
            this.scrollTop = null;
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
    PolicyService policyService;

    ReservedIp currentValue = null;
    private Long reservedIpIdForRemove = null;

    private static String CREATE_ACTION;
    private AppEvents.InitDetails initDetails;
    private Integer scrollTop;
}
