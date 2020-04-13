package ru.protei.portal.ui.region.client.activity.list;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.dto.RegionInfo;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.PeriodicTaskService;
import ru.protei.portal.ui.common.client.events.*;
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
    implements AbstractRegionListActivity, AbstractRegionItemActivity, AbstractRegionFilterActivity, Activity {

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
        if (!policyService.hasSystemScopeForPrivilege(En_Privilege.REGION_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

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
        query.setDistrictIds(
                filterView.districts().getValue().stream()
                        .map( (district)-> district.id )
                        .collect( Collectors.toSet() )
        );
        query.setSortField(En_SortField.name);
        query.setSortDir(En_SortDir.ASC);

        return query;
    };

    private AbstractRegionItemView makeView ( RegionInfo region ) {
        AbstractRegionItemView itemView = factory.get();
        itemView.setNumber( region.number );
        itemView.setName( region.name );
        itemView.setActivity(this);

        return itemView;
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
    Provider<AbstractRegionItemView > factory;
    @Inject
    RegionControllerAsync regionService;
    @Inject
    PolicyService policyService;

    @Inject
    PeriodicTaskService taskService;
    PeriodicTaskService.PeriodicTaskHandler fillViewHandler;

    private Map<AbstractRegionItemView, RegionInfo > itemViewToModel = new HashMap<>();
    private AppEvents.InitDetails init;
    private ProjectQuery query;
}