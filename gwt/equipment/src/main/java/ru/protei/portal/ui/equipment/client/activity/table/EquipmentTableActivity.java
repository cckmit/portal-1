package ru.protei.portal.ui.equipment.client.activity.table;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EquipmentServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.equipment.client.activity.filter.AbstractEquipmentFilterActivity;
import ru.protei.portal.ui.equipment.client.activity.filter.AbstractEquipmentFilterView;
import ru.protei.winter.web.common.client.events.SectionEvents;

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

        filterView.setActivity( this );
        view.getFilterContainer().add( filterView.asWidget() );

        pagerView.setPageSize( view.getPageSize() );
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

    @Event
    public void onShow( EquipmentEvents.Show event ) {
        init.parent.clear();
        init.parent.add( view.asWidget() );
        init.parent.add( pagerView.asWidget() );

        fireEvent( new ActionBarEvents.Add( lang.buttonCreate(), UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.EQUIPMENT ) );

        view.showElements();

        query = makeQuery();
        requestTotalCount();
    }

    @Event
    public void onCreateClicked( SectionEvents.Clicked event ) {
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
    public void onPageChanged( int page ) {
        pagerView.setCurrentPage( page+1 );
    }

    @Override
    public void onFirstClicked() {
        view.scrollTo( 0 );
    }

    @Override
    public void onLastClicked() {
        view.scrollTo( view.getPageCount()-1 );
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
            }
        });
    }

    private void showPreview ( Equipment value ) {
        if ( value == null ) {
            animation.closeDetails();
            view.showElements();
        } else {
            animation.showDetails();
            view.hideElements();
            fireEvent(new EquipmentEvents.ShowPreview(view.getPreviewContainer(), value));
        }
    }

    private EquipmentQuery makeQuery() {
        Long managerId = filterView.manager().getValue() == null ? null : filterView.manager().getValue().getId();
        En_SortDir sortDir = filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC;

        return new EquipmentQuery( filterView.name().getValue(), filterView.sortField().getValue(), sortDir,
                filterView.organizationCodes().getValue(), filterView.stages().getValue(), filterView.types().getValue(),
                filterView.classifierCode().getValue(), filterView.registerNumber().getValue(), managerId );
    }

    @Inject
    Lang lang;
    @Inject
    AbstractEquipmentTableView view;
    @Inject
    AbstractEquipmentFilterView filterView;

    @Inject
    EquipmentServiceAsync equipmentService;

    @Inject
    TableAnimation animation;

    @Inject
    AbstractPagerView pagerView;

    private AppEvents.InitDetails init;
    private EquipmentQuery query;
}