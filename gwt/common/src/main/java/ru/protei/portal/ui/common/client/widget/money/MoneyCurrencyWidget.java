package ru.protei.portal.ui.common.client.widget.money;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Currency;
import ru.protei.portal.core.model.struct.Money;
import ru.protei.portal.core.model.struct.MoneyWithCurrency;
import ru.protei.portal.ui.common.client.widget.selector.currency.CurrencyButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableLongBox;

public class MoneyCurrencyWidget extends Composite implements HasValue<MoneyWithCurrency>, HasEnabled {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initValidation();
    }

    @Override
    public MoneyWithCurrency getValue() {
        Money vMoney = new Money(moneyNatural.getValue(), moneyDecimal.getValue());
        En_Currency vCurrency = currency.getValue();
        return new MoneyWithCurrency(vMoney, vCurrency);
    }

    @Override
    public void setValue(MoneyWithCurrency value) {
        setValue(value, false);
    }

    @Override
    public void setValue(MoneyWithCurrency value, boolean fireEvents) {
        Money vMoney = value.getMoney() != null
                ? value.getMoney()
                : new Money(0L);
        En_Currency vCurrency = value.getCurrency() != null
                ? value.getCurrency()
                : defaultCurrency;
        moneyNatural.setValue(vMoney.getNatural());
        moneyDecimal.setValue(vMoney.getDecimal());
        currency.setValue(vCurrency);
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public boolean isEnabled() {
        return moneyNatural.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        moneyNatural.setEnabled(enabled);
        moneyDecimal.setEnabled(enabled);
        currency.setEnabled(enabled);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<MoneyWithCurrency> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void setDefaultCurrency(En_Currency value) {
        defaultCurrency = value;
        currency.setValue(value);
    }

    public void setEnsureDebugId(String debugId) {
        root.ensureDebugId(debugId);
    }

    private void initValidation() {
        moneyNatural.setValidationFunction(value -> value != null && value >= 0);
        moneyDecimal.setValidationFunction(value -> value != null && value >= 0 && value < 100);
    }

    @UiHandler({"moneyNatural", "moneyDecimal"})
    public void onMoneyChanged(ValueChangeEvent<Long> event) {
        MoneyWithCurrency value = getValue();
        ValueChangeEvent.fire(this, value);
    }

    @UiHandler("currency")
    public void onCurrencyChanged(ValueChangeEvent<En_Currency> event) {
        MoneyWithCurrency value = getValue();
        ValueChangeEvent.fire(this, value);
    }

    @UiField
    ValidableLongBox moneyNatural;
    @UiField
    ValidableLongBox moneyDecimal;
    @Inject
    @UiField(provided = true)
    CurrencyButtonSelector currency;
    @UiField
    HTMLPanel root;

    private En_Currency defaultCurrency;

    interface CostWithCurrencyViewUiBinder extends UiBinder<HTMLPanel, MoneyCurrencyWidget> {}
    private static CostWithCurrencyViewUiBinder ourUiBinder = GWT.create(CostWithCurrencyViewUiBinder.class);
}
