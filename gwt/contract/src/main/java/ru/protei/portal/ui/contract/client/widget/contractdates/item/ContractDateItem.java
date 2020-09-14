package ru.protei.portal.ui.contract.client.widget.contractdates.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.dict.En_ContractDatesType;
import ru.protei.portal.core.model.dict.En_Currency;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.core.model.struct.Money;
import ru.protei.portal.core.model.struct.MoneyWithCurrency;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.money.MoneyCurrencyWidget;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableDoubleBox;
import ru.protei.portal.ui.contract.client.widget.selector.ContractDatesTypeSelector;

import java.util.Date;
import java.util.function.Supplier;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class ContractDateItem
        extends Composite
        implements TakesValue<ContractDate>, HasCloseHandlers<ContractDateItem>
{
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        setTestAttributes();
        comment.getElement().setAttribute( "placeholder", lang.contractPaymentCommentPlaceholder() );
    }

    @Override
    public ContractDate getValue() {
        return value;
    }

    @Override
    public void setValue( ContractDate value ) {
         if ( value == null ) {
            value = new ContractDate();
        }
        this.value = value;

        type.setValue( value.getType() );
        date.setValue( value.getDate() );
        comment.setValue( value.getComment() );
        notify.setValue( value.isNotify() );
        moneyWithCurrency.setValue(new MoneyWithCurrency(value.getCost(), value.getCurrency()));
        moneyPercent.setValue(calculatePercent(value.getCost()));
    }

    public void setContractCostSupplier(Supplier<Money> contractCostSupplier) {
        this.contractCostSupplier = contractCostSupplier;
    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler<ContractDateItem> handler ) {
        return addHandler( handler, CloseEvent.getType() );
    }

    @UiHandler( "remove" )
    public void onRemoveClicked( ClickEvent event ) {
        event.preventDefault();
        CloseEvent.fire( this, this );
    }

    @UiHandler( "comment" )
    public void onChangeComment(KeyUpEvent event) {
        value.setComment(comment.getValue());
    }

    @UiHandler( "date" )
    public void onChangeDate(ValueChangeEvent<Date> event) {
        value.setDate(date.getValue());
    }

    @UiHandler( "moneyWithCurrency" )
    public void onChangeMoneyWithCurrency(ValueChangeEvent<MoneyWithCurrency> event) {
        MoneyWithCurrency mwc = moneyWithCurrency.getValue();
        Money cost = mwc != null ? mwc.getMoney() : null;
        En_Currency currency = mwc != null ? mwc.getCurrency() : null;
        Double costPercent = calculatePercent(cost);
        value.setCost(cost);
        value.setCurrency(currency);
        moneyPercent.setValue(costPercent, false);
    }

    @UiHandler( "moneyPercent" )
    public void onChangeMoneyPercent(ValueChangeEvent<Double> event) {
        Double costPercent = moneyPercent.getValue();
        Money cost = calculateCost(costPercent);
        value.setCost(cost);
        MoneyWithCurrency mwc = moneyWithCurrency.getValue();
        mwc.setMoney(cost);
        moneyWithCurrency.setValue(mwc, false);
    }

    @UiHandler( "type" )
    public void onChangeType(ValueChangeEvent<En_ContractDatesType> event) {
        value.setType(type.getValue());
    }

    @UiHandler( "notify" )
    public void onChangeNotify(ValueChangeEvent<Boolean> event) {
        value.setNotify(notify.getValue());
    }

    private void setTestAttributes() {
        root.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.DATE_ITEM.ITEM);
        type.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.DATE_ITEM.TYPE_BUTTON);
        date.getElement().getFirstChildElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.DATE_ITEM.DATE_CONTAINER);
        comment.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.DATE_ITEM.COMMENT_INPUT);
        notify.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.DATE_ITEM.NOTIFY_SWITCHER);
        remove.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.DATE_ITEM.REMOVE_BUTTON);
    }

    private Double calculatePercent(Money cost) {
        if (cost == null) {
            return null;
        }
        double ratio = (double) cost.getFull() / (double) contractCostSupplier.get().getFull();
        double percent = ratio * 100;
        return percent;
    }

    private Money calculateCost(Double percent) {
        if (percent == null) {
            return null;
        }
        double ratio = percent / 100;
        long cost = (long) ((double) contractCostSupplier.get().getFull() * ratio);
        return new Money(cost);
    }

    @UiField
    TextBox comment;
    @UiField
    Button remove;
    @Inject
    @UiField(provided = true)
    ContractDatesTypeSelector type;
    @Inject
    @UiField(provided = true)
    SinglePicker date;
    @UiField
    CheckBox notify;
    @UiField
    ValidableDoubleBox moneyPercent;
    @Inject
    @UiField(provided = true)
    MoneyCurrencyWidget moneyWithCurrency;

    @UiField
    Lang lang;
    @UiField
    HTMLPanel root;

    private ContractDate value = new ContractDate();
    private Supplier<Money> contractCostSupplier;

    interface PairItemUiBinder extends UiBinder< HTMLPanel, ContractDateItem> {}
    private static PairItemUiBinder ourUiBinder = GWT.create( PairItemUiBinder.class );
}