package ru.protei.portal.ui.company.client.widget.subscription.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.ui.company.client.widget.subscription.item.SubscriptionItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Список подписчиков на рассылку для компании
 */
public class SubscriptionList
        extends Composite
        implements HasValue<List<CompanySubscription>>
{
    public SubscriptionList() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public List<CompanySubscription> getValue() {
        return prepareValue();
    }

    @Override
    public void setValue( List<CompanySubscription> value ) {
        setValue( value, false );
    }

    @Override
    public void setValue( List<CompanySubscription > values, boolean fireEvents ) {
        clear();
        this.value = values == null ? new ArrayList<>() : values;
        value.forEach( this :: makeItemAndFillValue );
        addEmptyItem();

        if(fireEvents) {
            ValueChangeEvent.fire( this, this.value );
        }
    }


    public void clear() {
        container.clear();
        value.clear();
        modelToView.clear();
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<List<CompanySubscription>> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    private void makeItemAndFillValue( CompanySubscription subscription ) {
        SubscriptionItem subscriptionItemWidget = itemProvider.get();
        subscriptionItemWidget.setValue( subscription );
        subscriptionItemWidget.addCloseHandler( event -> {
            if ( container.getWidgetCount() < 2 ) {
                return;
            }
            container.remove( event.getTarget() );
            CompanySubscription remove = modelToView.remove( event.getTarget() );
            value.remove( remove );
        } );

        subscriptionItemWidget.addAddHandler( event -> {
            addEmptyItem();
            value.add( subscriptionItemWidget.getValue() );
        } );

        modelToView.put( subscriptionItemWidget, subscription );
        container.add( subscriptionItemWidget );
    }

    private void addEmptyItem() {
        CompanySubscription subscription = new CompanySubscription();
        makeItemAndFillValue( subscription );
    }

    private List<CompanySubscription> prepareValue() {
        return value.stream()
                .filter(value -> value.getEmail() != null && !value.getEmail().isEmpty() )
                .collect( toList() );
    }

    @UiField
    HTMLPanel container;
    @Inject
    Provider<SubscriptionItem> itemProvider;

    List<CompanySubscription> value = new ArrayList<>();
    Map<SubscriptionItem, CompanySubscription> modelToView = new HashMap<>();

    interface SubscriptionListUiBinder extends UiBinder< HTMLPanel, SubscriptionList > {}
    private static SubscriptionListUiBinder ourUiBinder = GWT.create( SubscriptionListUiBinder.class );

}