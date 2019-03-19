package ru.protei.portal.ui.contract.client.widget.contractdates.list;

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
import ru.brainworm.factory.core.datetimepicker.client.util.DateUtils;
import ru.protei.portal.core.model.dict.En_ContractDatesType;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.ui.contract.client.widget.contractdates.item.ContractDateItem;

import java.util.*;

public class ContractDatesList
        extends Composite
        implements HasValue<List<ContractDate>>
{
    public ContractDatesList() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public List<ContractDate> getValue() {
        return value;
    }

    @Override
    public void setValue(List<ContractDate> value ) {
        setValue( value, false );
    }

    @Override
    public void setValue(List<ContractDate> value, boolean fireEvents ) {
        clear();
        this.value = value == null ? new ArrayList<>() : value;
        for ( ContractDate items : this.value ) {
            makeItemAndFillValue(items);
        }

        if ( fireEvents ) {
            ValueChangeEvent.fire( this, this.value );
        }
    }

    public void clear() {
        container.clear();
        modelToView.clear();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<ContractDate>> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @UiHandler( "add" )
    public void onAddClicked( ClickEvent event ) {
        event.preventDefault();
        addEmptyItem();
        ValueChangeEvent.fire( this, value );
    }

    private void addEmptyItem() {
        ContractDate item = new ContractDate();
        item.setType(En_ContractDatesType.PAYMENT);
        item.setDate(DateUtils.setBeginOfDay(new Date()));

        value.add( item );

        makeItemAndFillValue( item );
    }

    private void makeItemAndFillValue(final ContractDate value ) {
        ContractDateItem itemWidget = itemFactory.get();
        itemWidget.setValue( value );
        itemWidget.addCloseHandler(event -> {
            container.remove( event.getTarget() );

            ContractDate remove = modelToView.remove( event.getTarget() );
            ContractDatesList.this.value.remove( remove );
        });

        modelToView.put( itemWidget, value );
        container.add( itemWidget );
    }

    @UiField
    FlowPanel container;
    @UiField
    Button add;

    @Inject
    Provider<ContractDateItem> itemFactory;
    List<ContractDate> value;
    Map<ContractDateItem, ContractDate> modelToView = new HashMap<>();

    interface ContractPeriodListUiBinder extends UiBinder< HTMLPanel, ContractDatesList> {}
    private static ContractPeriodListUiBinder ourUiBinder = GWT.create( ContractPeriodListUiBinder.class );

}