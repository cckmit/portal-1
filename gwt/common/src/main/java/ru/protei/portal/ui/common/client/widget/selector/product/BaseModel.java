package ru.protei.portal.ui.common.client.widget.selector.product;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ProductControllerAsync;
import ru.protei.portal.ui.common.client.util.SimpleProfiler;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public abstract class BaseModel implements SelectorModel<ProductShortView> {

    @Override
    public void onSelectorLoad( SelectorWithModel<ProductShortView> selector ) {
        if ( selector == null ) {
            return;
        }
        log.info( "onSelectorLoad(): BaseModel subscribers count="+CollectionUtils.size(subscribers) );
        subscribers.add( selector );
        if(!CollectionUtils.isEmpty( list )){
            selector.clearOptions();
            selector.fillOptions( list );
            return;
        }
        if ( selector.getValues() == null || selector.getValues().isEmpty() ) {
            refreshOptions();
        }
    }

    @Override
    public void onSelectorUnload( SelectorWithModel<ProductShortView> selector ) {
        if ( selector == null ) {
            return;
        }
        selector.clearOptions();
        subscribers.remove( selector );
        log.info( "onSelectorUnload(): BaseModel subscribers count="+CollectionUtils.size(subscribers) );

    }
    SimpleProfiler sp = new SimpleProfiler( SimpleProfiler.ON, ( message, currentTime ) -> {
        GWT.log("BaseModel " + getClass().getSimpleName() +" "+ message+ " t: " + currentTime);});
    protected abstract void failedToLoad();

    protected abstract ProductQuery getQuery();

    public void subscribe( SelectorWithModel<ProductShortView> selector) {
        subscribers.add(selector);
        selector.fillOptions(list);
    }

    private static final Logger log = Logger.getLogger( BaseModel.class.getName() );
    protected void refreshOptions() {
        if(requested) return;
        requested = true;
        sp.start( "start" );
        productService.getProductViewList(getQuery(), new RequestCallback<List<ProductShortView>>() {
            @Override
            public void onError(Throwable throwable) {
                requested = false; failedToLoad();
            }

            @Override
            public void onSuccess(List<ProductShortView> result) {
                requested = false;
                sp.check( "success" );
                list.clear();
                list.addAll(result);
                notifySubscribers();
                sp.stop( "done" );
            }
        });
    }

    protected void clearSubscribersOptions() {
        for (SelectorWithModel<ProductShortView> subscriber : subscribers) {
            subscriber.clearOptions();
        }
    }

    private void notifySubscribers() {
        sp.push();
        for (SelectorWithModel<ProductShortView> selector : subscribers) {
            selector.fillOptions(list);
            selector.refreshValue();
            sp.check( "fillOptions refreshValue " + selector.getClass().getSimpleName() );
        }
        sp.pop();
        sp.check( "notifySubscribers" );
    }

    @Inject
    public Lang lang;
    @Inject
    ProductControllerAsync productService;

    private boolean requested;
    private Set<SelectorWithModel<ProductShortView>> subscribers = new HashSet<>();
    private List<ProductShortView> list = new ArrayList<>();
}
