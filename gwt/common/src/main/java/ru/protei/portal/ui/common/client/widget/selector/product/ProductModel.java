package ru.protei.portal.ui.common.client.widget.selector.product;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ProductControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorModel;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;
import java.util.stream.Collectors;

public abstract class ProductModel implements Activity, SelectorModel<ProductShortView> {
    @Event
    public void onInit(AuthEvents.Success event) {
        clearSubscribersOptions();
    }

    @Event
    public void onProductListChanged(ProductEvents.ProductListChanged event) {
        clearSubscribersOptions();
    }

    @Override
    public void onSelectorLoad(SelectorWithModel<ProductShortView> selector) {
        getOptionsFromServer(selector);
    }

    public void getOptionsFromServer(SelectorWithModel<ProductShortView> selector) {
        if (selector == null) {
            return;
        }
        if (selector.getValues() == null || selector.getValues().isEmpty()) {
            requestOptions(selector, selectorToQuery.get(selector));
        }
    }

    @Override
    public void onSelectorUnload(SelectorWithModel<ProductShortView> selector) {
        if (selector == null) {
            return;
        }
        selector.clearOptions();
    }

    public void subscribe(SelectorWithModel<ProductShortView> selector, En_DevUnitState enDevUnitState, En_DevUnitType... enDevUnitTypes) {
        updateQuery(selector, enDevUnitState, enDevUnitTypes);
    }

    public void updateQuery(SelectorWithModel<ProductShortView> selector, En_DevUnitState enDevUnitState, En_DevUnitType... enDevUnitTypes) {
        ProductQuery query = makeQuery(enDevUnitState, enDevUnitTypes == null ? null : Arrays.stream(enDevUnitTypes).collect(Collectors.toSet()));
        selectorToQuery.put(selector, query);
    }

    public void updateQueryAndRequest(SelectorWithModel<ProductShortView> selector, En_DevUnitState enDevUnitState, En_DevUnitType... enDevUnitTypes) {
        updateQuery(selector, enDevUnitState, enDevUnitTypes);
        requestOptions(selector, selectorToQuery.get(selector));
    }

    private void clearSubscribersOptions() {
        for (SelectorWithModel<ProductShortView> subscriber : selectorToQuery.keySet()) {
            subscriber.clearOptions();
        }
    }

    private void requestOptions(SelectorWithModel<ProductShortView> selector, ProductQuery query) {
        productService.getProductViewList(query, new RequestCallback<List<ProductShortView>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(List<ProductShortView> options) {
                selector.fillOptions(options);
                selector.refreshValue();
            }
        } );
    }

    private ProductQuery makeQuery(En_DevUnitState enDevUnitState, Set<En_DevUnitType> enDevUnitTypes) {
        ProductQuery query = new ProductQuery();
        query.addTypes(enDevUnitTypes);
        query.setSortField(En_SortField.prod_name);
        query.setSortDir(En_SortDir.ASC);
        query.setState(enDevUnitState);
        return query;
    }

    @Inject
    Lang lang;

    @Inject
    ProductControllerAsync productService;

    private Map<SelectorWithModel<ProductShortView>, ProductQuery> selectorToQuery = new HashMap<>();
}
