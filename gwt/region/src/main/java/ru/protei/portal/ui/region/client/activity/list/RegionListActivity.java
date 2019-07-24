package ru.protei.portal.ui.region.client.activity.list;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.RegionInfo;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.PlateListAnimation;
import ru.protei.portal.ui.common.client.common.PeriodicTaskService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.En_RegionStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.region.client.activity.filter.AbstractRegionFilterActivity;
import ru.protei.portal.ui.region.client.activity.filter.AbstractRegionFilterView;
import ru.protei.portal.ui.region.client.activity.item.AbstractRegionItemActivity;
import ru.protei.portal.ui.region.client.activity.item.AbstractRegionItemView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Активность списка регионов
 */
public abstract class RegionListActivity
    implements AbstractRegionListActivity, AbstractRegionItemActivity, AbstractRegionFilterActivity, Activity
{

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        filterView.setActivity( this );
        view.getFilterContainer().add( filterView.asWidget() );
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onShow( RegionEvents.Show event ) {
        this.fireEvent(new AppEvents.InitPanelName(lang.regions()));
        init.parent.clear();
        init.parent.add(view.asWidget());

        fireEvent( new ActionBarEvents.Clear() );

        query = makeQuery();
        requestRegions();
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
    }

    @Override
    public void onCreateClicked( ) { fireEvent(new RegionEvents.Edit()); }

    @Override
    public void onEditClicked( AbstractRegionItemView itemView ) {
        fireEvent( new RegionEvents.Edit( itemViewToModel.get( itemView ).id ) );
    }

    @Override
    public void onPreviewClicked( AbstractRegionItemView itemView ) {
//        DevUnit value = itemViewToModel.get( itemView );
//        if ( value == null ) {
//            return;
//        }
//
//        fireEvent( new RegionEvents.ShowPreview( itemView.getPreviewContainer(), value ) );
//        animation.showPreview(itemView, (IsWidget) itemView.getPreviewContainer());
    }

    @Override
    public void onFavoriteClicked( AbstractRegionItemView itemView ) {
        Window.alert( "On favorite clicked" );
    }

    @Override
    public void onFilterChanged() {
        query = makeQuery();
        requestRegions();
    }

    private void requestRegions() {

        if ( fillViewHandler != null ) {
            fillViewHandler.cancel();
        }

        view.getChildContainer().clear();
        itemViewToModel.clear();

        regionService.getRegionList( query,
            new RequestCallback< List< RegionInfo > >() {
                @Override
                public void onError( Throwable throwable ) {
                    fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
                }

                @Override
                public void onSuccess( List< RegionInfo > result ) {
                    fillViewHandler = taskService.startPeriodicTask( result, fillViewer, 50, 50 );
                }
            }
        );
    }

    private ProjectQuery makeQuery() {
        query = new ProjectQuery();
        query.setSearchString(filterView.searchPattern().getValue());
        query.setStates( filterView.states().getValue() );
        query.setDistrictIds(
                filterView.districts().getValue().stream()
                        .map( (district)-> district.id )
                        .collect( Collectors.toSet() )
        );
/*        query.setDirectionId(
                filterView.direction().getValue() == null
                        ? null
                        : filterView.direction().getValue().id
        );*/
/*        query.setSortField(filterView.sortField().getValue());
        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);*/
        query.setSortField(En_SortField.name);
        query.setSortDir(En_SortDir.ASC);

        return query;
    };

    private AbstractRegionItemView makeView ( RegionInfo region ) {
        AbstractRegionItemView itemView = factory.get();
        itemView.setNumber( region.number );
        itemView.setName( region.name );
        itemView.setDetails( makeDetails( region ) );
        itemView.setActivity(this);
        itemView.setState( stateLang.getStateIcon( region.state ) );

        itemView.setEditEnabled( policyService.hasPrivilegeFor( En_Privilege.REGION_EDIT ) );

        return itemView;
    }

    private String makeDetails( RegionInfo region ) {
        String details = stateLang.getStateName( region.state );
        if ( region.state == null ) {
            return details;
        }

        switch ( region.state ) {
            case MARKETING:
                return details + (region.details == null ? "" : " ("+region.details+")");
            case DEPLOYMENT:
                return region.details == null ? details : region.details;
            default:
                return details;
        }
    }

     Consumer<RegionInfo> fillViewer = new Consumer<RegionInfo> () {
        @Override
        public void accept( RegionInfo product ) {
            AbstractRegionItemView itemView = makeView(product);

            itemViewToModel.put( itemView, product );
            view.getChildContainer().add( itemView.asWidget() );
        }
    };

    @Inject
    AbstractRegionListView view;
    @Inject
    AbstractRegionFilterView filterView;
    @Inject
    Lang lang;
    @Inject
    En_RegionStateLang stateLang;

    @Inject
    Provider<AbstractRegionItemView > factory;
    @Inject
    PolicyService policyService;
    @Inject
    RegionControllerAsync regionService;
    @Inject
    PlateListAnimation animation;

    @Inject
    PeriodicTaskService taskService;
    PeriodicTaskService.PeriodicTaskHandler fillViewHandler;

    private Map<AbstractRegionItemView, RegionInfo > itemViewToModel = new HashMap<>();
    private AppEvents.InitDetails init;
    private ProjectQuery query;
}