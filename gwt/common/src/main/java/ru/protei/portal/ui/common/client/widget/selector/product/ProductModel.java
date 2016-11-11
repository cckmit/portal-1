package ru.protei.portal.ui.common.client.widget.selector.product;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.service.ProductServiceAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель селектора продуктов
 */
public abstract class ProductModel implements Activity {

    @Event
    public void onInit( AuthEvents.Success event ) {
        refreshOptions();
    }

    @Event
    public void onProductListChanged( ProductEvents.ChangeModel event ) {
        refreshOptions();
    }

    public void subscribe( ModelSelector<DevUnit> selector ) {
        subscribers.add( selector );
        selector.fillOptions( list );
    }

    private void notifySubscribers() {
        for ( ModelSelector<DevUnit> selector : subscribers ) {
            selector.fillOptions( list );
            selector.refreshValue();
        }
    }

    private void refreshOptions() {

        productService.getProductList(null, new RequestCallback<List<DevUnit>>() {
            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onSuccess(List<DevUnit> groups) {
                list.clear();
                list.addAll(groups);

                notifySubscribers();
            }
        });
    }

    @Inject
    ProductServiceAsync productService;

    private List< DevUnit > list = new ArrayList<>();

    List< ModelSelector<DevUnit> > subscribers = new ArrayList<>();
}
