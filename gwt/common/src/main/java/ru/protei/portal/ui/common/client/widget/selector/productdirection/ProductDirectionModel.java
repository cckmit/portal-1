package ru.protei.portal.ui.common.client.widget.selector.productdirection;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.ProductDirectionQuery;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ProductControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель селектора продуктовых направлений
 */
public abstract class ProductDirectionModel implements Activity {

    @Event
    public void onInit( AuthEvents.Success event ) {
        refreshOptions();
    }

    public void subscribe( ModelSelector<ProductDirectionInfo> selector ) {
        subscribers.add( selector );
        selector.fillOptions( list );
    }

    private void notifySubscribers() {
        for ( ModelSelector< ProductDirectionInfo > selector : subscribers ) {
            selector.fillOptions( list );
            selector.refreshValue();
        }
    }

    private void refreshOptions() {

        productService.getProductDirectionList( new ProductDirectionQuery(null, En_SortField.prod_name, En_SortDir.ASC ),
                new RequestCallback<List<ProductDirectionInfo>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List<ProductDirectionInfo> options ) {
                list.clear();
                list.addAll( options );

                notifySubscribers();
            }
        } );
    }

    @Inject
    ProductControllerAsync productService;

    @Inject
    Lang lang;

    private List< ProductDirectionInfo > list = new ArrayList<>();

    List< ModelSelector< ProductDirectionInfo > > subscribers = new ArrayList<>();
}
