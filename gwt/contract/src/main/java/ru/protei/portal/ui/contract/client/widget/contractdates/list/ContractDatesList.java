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
import ru.protei.portal.core.model.dict.En_ContractDatesType;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.core.model.struct.Money;
import ru.protei.portal.ui.contract.client.widget.contractdates.item.ContractDateItem;

import java.util.*;
import java.util.function.Supplier;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

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
        showItems(this.value);
        showCostOverflowWarning(this.value);
        if (fireEvents) {
            ValueChangeEvent.fire(this, this.value);
        }
    }

    public void setContractCostSupplier(Supplier<Money> contractCostSupplier) {
        this.contractCostSupplier = contractCostSupplier;
    }

    public void onContractCostChanged(Money contractCost) {
        setValue(getValue());
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
        ContractDate item = makeEmptyItem();
        value.add(item);
        ContractDateItem itemWidget = makeItemWidget(item);
        modelToView.put(itemWidget, item);
        container.add(itemWidget);
        ValueChangeEvent.fire(this, value);
    }

    private void showItems(List<ContractDate> value) {
        for (ContractDate item : value) {
            ContractDateItem itemWidget = makeItemWidget(item);
            modelToView.put(itemWidget, item);
            container.add(itemWidget);
        }
    }

    private void showCostOverflowWarning(List<ContractDate> value) {
        long costOfPayments = stream(value)
            .map(ContractDate::getCost)
            .filter(Objects::nonNull)
            .mapToLong(Money::getFull)
            .sum();
        long costOfContract = contractCostSupplier.get().getFull();
        boolean isOverflow = costOfPayments > costOfContract;
        costOverflowWarning.setVisible(isOverflow);
    }

    private ContractDate makeEmptyItem() {
        ContractDate item = new ContractDate();
        item.setType(En_ContractDatesType.values()[0]);
        item.setDate(null);
        return item;
    }

    private ContractDateItem makeItemWidget(ContractDate contractDate) {
        ContractDateItem itemWidget = itemFactory.get();
        itemWidget.setContractCostSupplier(contractCostSupplier);
        itemWidget.setCostChangeListener(cost -> {
            showCostOverflowWarning(value);
        });
        itemWidget.setValue(contractDate);
        itemWidget.addCloseHandler(event -> {
            container.remove(event.getTarget());
            ContractDate remove = modelToView.remove(event.getTarget());
            ContractDatesList.this.value.remove(remove);
        });
        return itemWidget;
    }

    public void setEnsureDebugId(String debugId) {
        add.ensureDebugId(debugId);
    }

    @UiField
    FlowPanel container;
    @UiField
    Button add;
    @UiField
    HTMLPanel costOverflowWarning;

    @Inject
    Provider<ContractDateItem> itemFactory;
    List<ContractDate> value;
    Map<ContractDateItem, ContractDate> modelToView = new HashMap<>();
    private Supplier<Money> contractCostSupplier;

    interface ContractPeriodListUiBinder extends UiBinder< HTMLPanel, ContractDatesList> {}
    private static ContractPeriodListUiBinder ourUiBinder = GWT.create( ContractPeriodListUiBinder.class );

}