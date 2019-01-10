package ru.protei.portal.ui.common.client.widget.selector.product;

import com.google.inject.Inject;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ProductControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.HasSelectableValues;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class BaseModel implements SelectorModel<ProductShortView> {

    @Override
    public void onSelectorLoad( HasSelectableValues<ProductShortView> selector ) {
        if ( selector == null ) {
            return;
        }
        if ( selector.getValues() == null || selector.getValues().isEmpty() ) {
            refreshOptions();
        }
    }

    protected abstract void failedToLoad();

    protected abstract ProductQuery getQuery();

    public void subscribe(ModelSelector<ProductShortView> selector) {
        subscribers.add(selector);
        selector.fillOptions(list);
    }

    private static final Logger log = Logger.getLogger( BaseModel.class.getName() );
    protected void refreshOptions() {
        long start = System.currentTimeMillis();
        log.info( "refreshOptions(): BaseModel start " );
        productService.getProductViewList(getQuery(), new RequestCallback<List<ProductShortView>>() {
            @Override
            public void onError(Throwable throwable) {
                failedToLoad();
            }

            @Override
            public void onSuccess(List<ProductShortView> result) {
                log.info( "refreshOptions(): BaseModel success " + (start - System.currentTimeMillis() ) );
                list.clear();
                list.addAll(result);
                notifySubscribers();
                log.info( "refreshOptions(): BaseModel done " + (start - System.currentTimeMillis() ) );
            }
        });
    }

    protected void clearSubscribersOptions() {
        for (ModelSelector<ProductShortView> subscriber : subscribers) {
            subscriber.clearOptions();
        }
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
