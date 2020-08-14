package ru.protei.portal.ui.common.client.widget.money;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Currency;
import ru.protei.portal.core.model.struct.Money;
import ru.protei.portal.core.model.struct.MoneyWithCurrency;
import ru.protei.portal.ui.common.client.widget.selector.currency.CurrencyButtonSelector;

import static ru.protei.portal.core.model.helper.NullUtils.defaultIfNull;

public class MoneyCurrencyWidget extends Composite implements HasValue<MoneyWithCurrency>, HasEnabled {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
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
        moneyNatural.setValue(defaultIfNull(() -> value.getMoney().getNatural(), 0L));
        moneyDecimal.setValue(defaultIfNull(() -> value.getMoney().getDecimal(), 0L));
        currency.setValue(defaultIfNull(value::getCurrency, defaultCurrency));
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
    LongBox moneyNatural;
    @UiField
    LongBox moneyDecimal;
    @Inject
    @UiField(provided = true)
    CurrencyButtonSelector currency;
    @UiField
    HTMLPanel root;

    private En_Currency defaultCurrency;

    interface CostWithCurrencyViewUiBinder extends UiBinder<HTMLPanel, MoneyCurrencyWidget> {}
    private static CostWithCurrencyViewUiBinder ourUiBinder = GWT.create(CostWithCurrencyViewUiBinder.class);
}
