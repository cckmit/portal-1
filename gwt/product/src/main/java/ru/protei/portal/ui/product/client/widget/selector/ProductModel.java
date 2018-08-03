package ru.protei.portal.ui.product.client.widget.selector;

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
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ProductControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;

public abstract class ProductModel implements Activity {

    @Inject
    public void init() {
        query = new ProductQuery();
        query.addType(En_DevUnitType.PRODUCT);
        query.setState(En_DevUnitState.ACTIVE);
        query.setSortField(En_SortField.prod_name);
        query.setSortDir(En_SortDir.ASC);
    }

    @Event
    public void onInit(AuthEvents.Success event) {
        refreshOptions();
    }

    public void subscribe(ModelSelector<ProductShortView> selector) {
        subscribers.add(selector);
        selector.fillOptions(list);
    }

    private void notifySubscribers() {
        for (ModelSelector<ProductShortView> selector : subscribers) {
            selector.fillOptions(list);
            selector.refreshValue();
        }
    }

    private void refreshOptions() {
        productService.getProductViewList(query, new RequestCallback<List<ProductShortView>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(List<ProductShortView> result) {
                list.clear();
                list.addAll(result);
                notifySubscribers();
            }
        });
    }

    @Inject
    Lang lang;
    @Inject
    ProductControllerAsync productService;

    private ProductQuery query;
    private List<ModelSelector<ProductShortView>> subscribers = new ArrayList<>();
    private List<ProductShortView> list = new ArrayList<>();
}
