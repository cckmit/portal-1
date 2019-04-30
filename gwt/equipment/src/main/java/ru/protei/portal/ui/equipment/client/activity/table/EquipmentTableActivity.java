package ru.protei.portal.ui.equipment.client.activity.table;

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
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EquipmentControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.equipment.client.activity.filter.AbstractEquipmentFilterActivity;
import ru.protei.portal.ui.equipment.client.activity.filter.AbstractEquipmentFilterView;

import java.util.List;

/**
 * Активность таблицы оборудования
 */
public abstract class EquipmentTableActivity
        implements AbstractEquipmentTableActivity, AbstractEquipmentFilterActivity,
        AbstractPagerActivity, Activity
{

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
        view.setAnimation( animation );

        CREATE_ACTION = lang.buttonCreate();
        filterView.setActivity( this );
        view.getFilterContainer().add( filterView.asWidget() );

        pagerView.setActivity( this );
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.init = initDetails;
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event(Type.FILL_CONTENT)
    public void onShow( EquipmentEvents.Show event ) {
        init.parent.clear();
        init.parent.add( view.asWidget() );
        view.getPagerContainer().add( pagerView.asWidget() );

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.EQUIPMENT_CREATE ) ?
                new ActionBarEvents.Add( CREATE_ACTION, null, UiConstants.ActionBarIdentity.EQUIPMENT ) :
                new ActionBarEvents.Clear()
        );

        query = makeQuery();

        requestTotalCount();
    }

    @Event
    public void onCreateClicked( ActionBarEvents.Clicked event ) {
        if ( !UiConstants.ActionBarIdentity.EQUIPMENT.equals( event.identity ) ) {
            return;
        }

        fireEvent( new EquipmentEvents.Edit( null ) );
    }

    @Override
    public void onItemClicked ( Equipment value ) {
        showPreview( value );
    }

    @Override
    public void onEditClicked(Equipment value ) {
        persistScrollTopPosition();
        fireEvent(EquipmentEvents.Edit.byId(value.getId()));
    }

    @Override
    public void onFilterChanged() {
        query = makeQuery();
        requestTotalCount();
    }

    @Override
    public void loadData( int offset, int limit, AsyncCallback<List<Equipment>> asyncCallback ) {
        query.setOffset( offset );
        query.setLimit( limit );

        equipmentService.getEquipments( query, new RequestCallback<List<Equipment>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                asyncCallback.onFailure( throwable );
            }

            @Override
            public void onSuccess( List<Equipment> persons ) {
                asyncCallback.onSuccess( persons );
            }
        } );
    }

    @Override
    public void onPageChanged(int page) {
        pagerView.setCurrentPage(page);
    }

    @Override
    public void onPageSelected(int page) {
        view.scrollTo(page);
    }

    private void persistScrollTopPosition() {
        scrollTop = Window.getScrollTop();
    }

    private void restoreScrollTopPositionOrClearSelection() {
        if (scrollTop == null) {
            view.clearSelection();
            return;
        }
        if (scrollTop <= RootPanel.get(DebugIds.DEBUG_ID_PREFIX + DebugIds.APP_VIEW.GLOBAL_CONTAINER).getOffsetHeight() - Window.getClientHeight()) {
            Window.scrollTo(0, scrollTop);
            scrollTop = null;
        }
    }

    private void requestTotalCount() {
        view.clearRecords();
        animation.closeDetails();

        equipmentService.getEquipmentCount(query, new RequestCallback<Long>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Long count) {
                view.setRecordCount( count );
                pagerView.setTotalPages( view.getPageCount() );
                pagerView.setTotalCount( count );
                restoreScrollTopPositionOrClearSelection();
            }
        });
    }

    private void showPreview ( Equipment value ) {
        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new EquipmentEvents.ShowPreview(view.getPreviewContainer(), value));
        }
    }

    private EquipmentQuery makeQuery() {
        Long managerId = filterView.manager().getValue() == null ? null : filterView.manager().getValue().getId();
        En_SortDir sortDir = filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC;
        Long equipmentId = filterView.equipment().getValue() == null ? null : filterView.equipment().getValue().getId();
        return new EquipmentQuery( filterView.name().getValue(), filterView.sortField().getValue(), sortDir,
                filterView.organizationCodes().getValue(), filterView.types().getValue(),
                filterView.classifierCode().getValue(), filterView.registerNumber().getValue(), managerId, equipmentId );
    }

    @Inject
    Lang lang;
    @Inject
    AbstractEquipmentTableView view;
    @Inject
    AbstractEquipmentFilterView filterView;

    @Inject
    EquipmentControllerAsync equipmentService;

    @Inject
    TableAnimation animation;
    @Inject
    PolicyService policyService;
    @Inject
    AbstractPagerView pagerView;

    private AppEvents.InitDetails init;
    private EquipmentQuery query;
    private Integer scrollTop;
    private static String CREATE_ACTION;
}
