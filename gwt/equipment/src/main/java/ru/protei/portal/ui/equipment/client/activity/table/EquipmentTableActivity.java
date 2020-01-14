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
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.equipment.client.activity.filter.AbstractEquipmentFilterActivity;
import ru.protei.portal.ui.equipment.client.activity.filter.AbstractEquipmentFilterView;
import ru.protei.winter.core.utils.beans.SearchResult;

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
        if (!policyService.hasPrivilegeFor(En_Privilege.EQUIPMENT_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        init.parent.clear();
        init.parent.add( view.asWidget() );
        view.getPagerContainer().add( pagerView.asWidget() );

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.EQUIPMENT_CREATE ) ?
                new ActionBarEvents.Add( CREATE_ACTION, null, UiConstants.ActionBarIdentity.EQUIPMENT ) :
                new ActionBarEvents.Clear()
        );

        clearScroll(event);

        query = makeQuery();

        loadTable();
    }

    @Event
    public void onCreateClicked( ActionBarEvents.Clicked event ) {
        if ( !UiConstants.ActionBarIdentity.EQUIPMENT.equals( event.identity ) ) {
            return;
        }

        view.clearSelection();
        selectedEquipment = null;

        fireEvent( new EquipmentEvents.Edit( null ) );
    }

    @Override
    public void onItemClicked ( Equipment value ) {
        if (selectedEquipment != null) view.removeSelection(selectedEquipment);
        selectedEquipment = value;
        showPreview( value );
    }

    @Override
    public void onEditClicked(Equipment value ) {
        selectedEquipment = value;
        persistScrollTopPosition();
        fireEvent(EquipmentEvents.Edit.byId(value.getId()));
    }

    @Override
    public void onFilterChanged() {
        query = makeQuery();
        loadTable();
    }

    @Override
    public void onPageChanged(int page) {
        pagerView.setCurrentPage(page);
    }

    @Override
    public void onPageSelected(int page) {
        view.scrollTo(page);
    }

    @Override
    public void loadData( int offset, int limit, AsyncCallback<List<Equipment>> asyncCallback ) {
        boolean isFirstChunk = offset == 0;
        query.setOffset(offset);
        query.setLimit(limit);
        equipmentService.getEquipments(query, new FluentCallback<SearchResult<Equipment>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
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

    private void loadTable() {
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
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

    private void clearScroll(EquipmentEvents.Show event) {
        if (event.clearScroll) {
            event.clearScroll = false;
            this.scrollTop = null;
        }
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

    private Equipment selectedEquipment;
    private AppEvents.InitDetails init;
    private EquipmentQuery query;
    private Integer scrollTop;
    private static String CREATE_ACTION;
}
