package ru.protei.portal.ui.common.client.widget.selector.equipment;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.EquipmentEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EquipmentControllerAsync;
import ru.protei.portal.ui.common.client.widget.components.client.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.widget.components.client.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.widget.components.client.selector.AsyncSearchSelectorModel;
import ru.protei.portal.ui.common.client.widget.components.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.widget.components.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.widget.selector.base.LifecycleSelectorModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class EquipmentModel implements Activity, AsyncSelectorModel<EquipmentShortView> {

    public EquipmentModel() {
        query = makeQuery();
        cache.setLoadHandler(makeLoadHandler(query));
    }

    @Event
    public void onInit(AuthEvents.Success event) {
        cache.clearCache();
    }

    @Event
    public void onEquipmentListChanged(EquipmentEvents.ChangeModel event) {
        cache.clearCache();
    }

    @Override
    public EquipmentShortView get( int elementIndex, LoadingHandler loadingHandler ) {
        return cache.get( elementIndex, loadingHandler );
    }

    private SelectorDataCacheLoadHandler<EquipmentShortView> makeLoadHandler( final EquipmentQuery query) {
        return new SelectorDataCacheLoadHandler() {
            @Override
            public void loadData( int offset, int limit, AsyncCallback handler ) {
                query.setOffset( offset );
                query.setLimit( limit );
                equipmentService.equipmentOptionList( query, new FluentCallback<List<EquipmentShortView>>()
                        .withErrorMessage( lang.errGetList() )
                        .withSuccess( options -> handler.onSuccess( options ) ) );
            }
        };
    }

    private EquipmentQuery makeQuery() {
        EquipmentQuery query = new EquipmentQuery();
        query.setTypes(defaultEquipmentTypes);
        return query;
    }

    public void setVisibleTypes(Set<En_EquipmentType> types) {
        query.setTypes( types );
    }

    public void setProjectId(Long projectId) {
        query.setProjectId( projectId );
    }

    @Inject
    EquipmentControllerAsync equipmentService;
    @Inject
    Lang lang;


    private Set<En_EquipmentType> defaultEquipmentTypes = new HashSet<>(
            Arrays.asList(En_EquipmentType.ASSEMBLY_UNIT, En_EquipmentType.COMPLEX, En_EquipmentType.PRODUCT)
    );

    EquipmentQuery query;
    private SelectorDataCache<EquipmentShortView> cache = new SelectorDataCache<>();
}
