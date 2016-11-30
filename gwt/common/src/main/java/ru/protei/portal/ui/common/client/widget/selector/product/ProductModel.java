package ru.protei.portal.ui.common.client.widget.selector.product;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
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

    public void subscribe( ModelSelector< ProductShortView > selector ) {
        subscribers.add( selector );
        selector.fillOptions( list );
    }

    private void notifySubscribers() {
        for ( ModelSelector< ProductShortView > selector : subscribers ) {
            selector.fillOptions( list );
            selector.refreshValue();
        }
    }

    private void refreshOptions() {

        productService.getProductViewList( new ProductQuery( null, null, En_SortField.prod_name, En_SortDir.ASC ),
                new RequestCallback<List<ProductShortView>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List<ProductShortView> options ) {
                list.clear();
                list.addAll( options );

                notifySubscribers();
            }
        } );
    }

    @Inject
    ProductServiceAsync productService;

    @Inject
    Lang lang;

    private List< ProductShortView > list = new ArrayList<>();

    List< ModelSelector< ProductShortView > > subscribers = new ArrayList<>();
}
