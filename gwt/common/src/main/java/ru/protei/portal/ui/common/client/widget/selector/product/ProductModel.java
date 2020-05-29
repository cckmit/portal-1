package ru.protei.portal.ui.common.client.widget.selector.product;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ProductControllerAsync;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ProductModel implements Activity,
        AsyncSelectorModel<ProductShortView> {

    public ProductModel() {
        query = makeQuery();
        cache.setLoadHandler(makeLoadHandler(query));
    }

    @Event
    public void onInit(AuthEvents.Success event) {
        cache.clearCache();
    }

    @Event
    public void onProductListChanged(ProductEvents.ProductListChanged event) {
        cache.clearCache();
    }

    @Override
    public ProductShortView get( int elementIndex, LoadingHandler loadingHandler ) {
        return cache.get( elementIndex, loadingHandler );
    }

    public void setUnitState( En_DevUnitState devUnitState ) {
        cache.clearCache();
        query.setState( devUnitState );
    }

    public void setUnitTypes( En_DevUnitType... enDevUnitTypes ) {
        cache.clearCache();
        query.setTypes( enDevUnitTypes == null ? null : Arrays.stream(enDevUnitTypes).collect(Collectors.toSet()) );
    }

    public void setDirectionId(Long directionId) {
        cache.clearCache();
        query.setDirectionId(directionId);
    }

    public void setPlatformIds(Set<Long> platformIds) {
        cache.clearCache();
        query.setPlatformIds(platformIds);
    }

    private SelectorDataCacheLoadHandler<ProductShortView> makeLoadHandler( final ProductQuery query) {
        return new SelectorDataCacheLoadHandler() {
            @Override
            public void loadData( int offset, int limit, AsyncCallback handler ) {
                query.setOffset(offset);
                query.setLimit(limit);
                productService.getProductViewList( query, new RequestCallback<List<ProductShortView>>() {
                    @Override
                    public void onError( Throwable throwable ) {
                        fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
                    }

                    @Override
                    public void onSuccess( List<ProductShortView> options ) {
                        handler.onSuccess( options );
                    }
                } );
            }
        };
    }

    private ProductQuery makeQuery() {
        ProductQuery query = new ProductQuery();
        query.setSortField(En_SortField.prod_name);
        query.setSortDir(En_SortDir.ASC);
        return query;
    }

    @Inject
    Lang lang;

    @Inject
    ProductControllerAsync productService;

    private SelectorDataCache<ProductShortView> cache = new SelectorDataCache<>();
    private ProductQuery query;
}
