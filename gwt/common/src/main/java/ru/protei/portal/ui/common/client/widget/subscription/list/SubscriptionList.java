package ru.protei.portal.ui.common.client.widget.subscription.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.ui.common.client.widget.subscription.item.SubscriptionItem;
import ru.protei.portal.ui.common.client.widget.subscription.model.Subscription;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * Список подписчиков на рассылку для компании
 */
public class SubscriptionList
        extends Composite
        implements HasValue<List<Subscription>>, HasValidable
{
    public SubscriptionList() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public List<Subscription> getValue() {
        return prepareValue();
    }

    @Override
    public void setValue( List<Subscription> value ) {
        setValue( value, false );
    }

    @Override
    public void setValue( List<Subscription > values, boolean fireEvents ) {
        clear();
        this.value = values == null ? new ArrayList<>() : values;
        value.forEach( this :: makeItemAndFillValue );
        addEmptyItem();

        if(fireEvents) {
            ValueChangeEvent.fire( this, this.value );
        }
    }

    @Override
    public void setValid(boolean isValid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isValid() {
        return modelToView.keySet().stream().allMatch(SubscriptionItem::isValid);
    }

    public void clear() {
        container.clear();
        value.clear();
        modelToView.clear();
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<List<Subscription>> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    private void makeItemAndFillValue( Subscription subscription ) {
        SubscriptionItem subscriptionItemWidget = itemProvider.get();
        subscriptionItemWidget.setValue( subscription );
        subscriptionItemWidget.addCloseHandler( event -> {
            if ( container.getWidgetCount() == 1 ) {
                return;
            }
            container.remove( event.getTarget() );
            Subscription remove = modelToView.remove( event.getTarget() );
            value.remove( remove );
            boolean isHasEmptyItem = modelToView.values().stream().anyMatch(s -> s.getEmail() == null || s.getEmail().isEmpty());
            if(!isHasEmptyItem)
                addEmptyItem();
        } );

        subscriptionItemWidget.addAddHandler( event -> {
            addEmptyItem();
            value.add( subscriptionItemWidget.getValue() );
        } );

        modelToView.put( subscriptionItemWidget, subscription );
        container.add( subscriptionItemWidget );
    }

    private void addEmptyItem() {
        Subscription subscription = new Subscription();
        makeItemAndFillValue( subscription );
    }

    private List<Subscription> prepareValue() {
        Collection<Subscription> c = value.stream()
                .filter(value -> value.getEmail() != null && !value.getEmail().isEmpty() )
                .collect(toMap( Subscription::getEmail, p -> p, (p, q) -> p)) //filter by unique email
                .values();

        return new ArrayList<>(c);
    }

    @UiField
    HTMLPanel container;
    @Inject
    Provider<SubscriptionItem> itemProvider;

    List<Subscription> value = new ArrayList<>();
    Map<SubscriptionItem, Subscription> modelToView = new HashMap<>();

    interface SubscriptionListUiBinder extends UiBinder< HTMLPanel, SubscriptionList> {}
    private static SubscriptionListUiBinder ourUiBinder = GWT.create( SubscriptionListUiBinder.class );

}