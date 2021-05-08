package ru.protei.portal.ui.delivery.client.widget.kit.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.delivery.client.widget.kit.item.DeliveryKitItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DeliveryKitList
        extends Composite
        implements HasValue<List<Kit>>, HasValidable
{
    public DeliveryKitList() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public List<Kit> getValue() {
        return value;
    }

    @Override
    public void setValue(List<Kit> value ) {
        setValue( value, false );
    }

    @Override
    public void setValue(List<Kit> value, boolean fireEvents ) {
        clear();
        this.value = value == null ? new ArrayList<>() : value;
        for ( Kit items : this.value ) {
            makeItemAndFillValue(items);
        }

        isValid();

        if ( fireEvents ) {
            ValueChangeEvent.fire( this, this.value );
        }
    }

    public void clear() {
        container.clear();
        modelToView.clear();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<Kit>> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @Override
    public void setValid(boolean isValid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isValid() {
        return modelToView.keySet().stream().allMatch(DeliveryKitItem::isValid);
    }

    @UiHandler( "add" )
    public void onAddClicked( ClickEvent event ) {
        event.preventDefault();
        addEmptyItem();
        ValueChangeEvent.fire( this, value );
    }

    public void setEmptyItemProvider(Supplier<Kit> provider) {
        emptyKitProvider = provider;
    }

    private Supplier<Kit> emptyKitProvider;

    private void addEmptyItem() {
        Kit item = emptyKitProvider.get();
        value.add(item);
        makeItemAndFillValue( item );
    }

    private void makeItemAndFillValue(final Kit value ) {
        DeliveryKitItem itemWidget = itemFactory.get();
        itemWidget.setValue( value );
        itemWidget.addCloseHandler(event -> {
            container.remove( event.getTarget() );

            Kit remove = modelToView.remove( event.getTarget() );
            DeliveryKitList.this.value.remove( remove );
        });

        modelToView.put( itemWidget, value );
        container.add( itemWidget );
    }

    public void setEnsureDebugId(String debugId) {
        add.ensureDebugId(debugId);
    }

    @UiField
    FlowPanel container;
    @UiField
    Button add;
    @UiField
    Lang lang;

    @Inject
    Provider<DeliveryKitItem> itemFactory;
    List<Kit> value;
    Map<DeliveryKitItem, Kit> modelToView = new HashMap<>();

    interface DeliveryKitListUiBinder extends UiBinder< HTMLPanel, DeliveryKitList> {}
    private static DeliveryKitListUiBinder ourUiBinder = GWT.create( DeliveryKitListUiBinder.class );

}