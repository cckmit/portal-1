package ru.protei.portal.ui.common.client.widget.selector.product;

import com.google.inject.Inject;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ProductControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseModel {

    protected abstract void failedToLoad();

    protected abstract ProductQuery getQuery();

    public void subscribe(ModelSelector<ProductShortView> selector) {
        subscribers.add(selector);
        selector.fillOptions(list);
    }

    protected void refreshOptions() {
        productService.getProductViewList(getQuery(), new RequestCallback<List<ProductShortView>>() {
            @Override
            public void onError(Throwable throwable) {
                failedToLoad();
            }

            @Override
            public void onSuccess(List<ProductShortView> result) {
                list.clear();
                list.addAll(result);
                notifySubscribers();
            }
        });
    }

    private void notifySubscribers() {
        for (ModelSelector<ProductShortView> selector : subscribers) {
            selector.fillOptions(list);
            selector.refreshValue();
        }
    }

    @Inject
    public Lang lang;
    @Inject
    ProductControllerAsync productService;

    private List<ModelSelector<ProductShortView>> subscribers = new ArrayList<>();
    private List<ProductShortView> list = new ArrayList<>();
}
