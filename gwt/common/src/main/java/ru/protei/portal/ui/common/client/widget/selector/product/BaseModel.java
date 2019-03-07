package ru.protei.portal.ui.common.client.widget.selector.product;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorModel;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class BaseModel implements SelectorModel<ProductShortView>, Activity {

    @Override
    public void onSelectorLoad( SelectorWithModel<ProductShortView> selector ) {
        if ( selector == null ) {
            return;
        }
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

    }
    @Event
    public void onInit(AuthEvents.Success event) {
        clearSubscribersOptions();
    }

    @Event
    public void onProductListChanged( ProductEvents.ProductListChanged event ) {
        refreshOptions();
    }

    protected void failedToLoad() {
        fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
    }
    public void setOptions(List<ProductShortView> result) {
        list.clear();
        list.addAll(result);
        notifySubscribers();
    }
    protected abstract void refreshOptions();
    protected void clearSubscribersOptions() {
        for (SelectorWithModel<ProductShortView> subscriber : subscribers) {
            subscriber.clearOptions();
        }
    }
    private void notifySubscribers() {
        for (SelectorWithModel<ProductShortView> selector : subscribers) {
            selector.fillOptions(list);
            selector.refreshValue();
        }
    }
    @Inject
    public Lang lang;

    private Set<SelectorWithModel<ProductShortView>> subscribers = new HashSet<>();
    private List<ProductShortView> list = new ArrayList<>();
}
