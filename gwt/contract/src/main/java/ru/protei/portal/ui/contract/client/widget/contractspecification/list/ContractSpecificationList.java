package ru.protei.portal.ui.contract.client.widget.contractspecification.list;

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
import ru.protei.portal.core.model.ent.ContractSpecification;
import ru.protei.portal.ui.contract.client.widget.contractspecification.item.ContractSpecificationItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContractSpecificationList
        extends Composite
        implements HasValue<List<ContractSpecification>>
{
    public ContractSpecificationList() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public List<ContractSpecification> getValue() {
        return value;
    }

    @Override
    public void setValue(List<ContractSpecification> value ) {
        setValue( value, false );
    }

    @Override
    public void setValue(List<ContractSpecification> value, boolean fireEvents ) {
        clear();
        this.value = value == null ? new ArrayList<>() : value;
        for ( ContractSpecification items : this.value ) {
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
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<ContractSpecification>> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @UiHandler( "add" )
    public void onAddClicked( ClickEvent event ) {
        event.preventDefault();
        addEmptyItem();
        ValueChangeEvent.fire( this, value );
    }

    private void addEmptyItem() {
        ContractSpecification item = new ContractSpecification();
        value.add( item );
        makeItemAndFillValue( item );
    }

    private void makeItemAndFillValue(final ContractSpecification value ) {
        ContractSpecificationItem itemWidget = itemFactory.get();
        itemWidget.setValue( value );
        itemWidget.addCloseHandler(event -> {
            container.remove( event.getTarget() );

            ContractSpecification remove = modelToView.remove( event.getTarget() );
            ContractSpecificationList.this.value.remove( remove );
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

    @Inject
    Provider<ContractSpecificationItem> itemFactory;
    List<ContractSpecification> value;
    Map<ContractSpecificationItem, ContractSpecification> modelToView = new HashMap<>();

    interface ContractSpecificationListUiBinder extends UiBinder< HTMLPanel, ContractSpecificationList> {}
    private static ContractSpecificationListUiBinder ourUiBinder = GWT.create( ContractSpecificationListUiBinder.class );

}