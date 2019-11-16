package ru.protei.portal.ui.common.client.widget.selector.product;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ProductControllerAsync;
import ru.protei.portal.ui.common.client.widget.components.client.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.widget.components.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.widget.components.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.widget.components.client.selector.SelectorItemRenderer;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorModel;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ProductModel implements Activity,
        AsyncSelectorModel<ProductShortView> {

    public ProductModel() {
        query = makeQuery();
        cache.setLoadHandler(makeLoadHandler(query, cache));
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
        query.setState( devUnitState );
    }
    public void setUnitTypes( En_DevUnitType... enDevUnitTypes ) {
        query.addTypes( enDevUnitTypes == null ? null : Arrays.stream(enDevUnitTypes).collect(Collectors.toSet()) );
    }

    private InfiniteLoadHandler<ProductShortView> makeLoadHandler( final ProductQuery query, final SelectorDataCache<ProductShortView> cache) {
        return new InfiniteLoadHandler() {
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
                        if (options.size() < limit) cache.setTotal( offset + options.size() );

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
