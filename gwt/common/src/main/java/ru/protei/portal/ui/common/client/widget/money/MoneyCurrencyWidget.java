package ru.protei.portal.ui.common.client.widget.money;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
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
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.widget.selector.currency.CurrencyButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableMoneyBox;

import java.util.function.Function;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class MoneyCurrencyWidget extends Composite implements HasValue<MoneyWithCurrency>, HasEnabled {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initValidation();
        ensureDebugIds();
    }

    @Override
    public MoneyWithCurrency getValue() {
        Money vMoney = money.getValue();
        En_Currency vCurrency = currency.getValue();
        return new MoneyWithCurrency(vMoney, vCurrency);
    }

    @Override
    public void setValue(MoneyWithCurrency value) {
        setValue(value, false);
    }

    @Override
    public void setValue(MoneyWithCurrency value, boolean fireEvents) {
        Money vMoney = value.getMoney();
        En_Currency vCurrency = value.getCurrency() != null
                ? value.getCurrency()
                : defaultCurrency;
        money.setValue(vMoney);
        currency.setValue(vCurrency);
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        money.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.COST_WITH_CURRENCY.MONEY_AMOUNT_INPUT);
        currency.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.COST_WITH_CURRENCY.CURRENCY_SELECTOR);
    }


    @Override
    public boolean isEnabled() {
        return money.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        money.setEnabled(enabled);
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

    public void setMoneyValidationFunction(Function<Money, Boolean> validationFunction) {
        money.setValidationFunction(validationFunction);
    }

    @UiHandler("money")
    public void onMoneyChanged(ValueChangeEvent<Money> event) {
        MoneyWithCurrency value = getValue();
        ValueChangeEvent.fire(this, value);
    }

    @UiHandler("currency")
    public void onCurrencyChanged(ValueChangeEvent<En_Currency> event) {
        MoneyWithCurrency value = getValue();
        ValueChangeEvent.fire(this, value);
    }

    private void initValidation() {
        setMoneyValidationFunction(value -> value != null && value.getFull() >= 0);
    }

    @UiField
    ValidableMoneyBox money;
    @Inject
    @UiField(provided = true)
    CurrencyButtonSelector currency;
    @UiField
    HTMLPanel root;

    private En_Currency defaultCurrency;

    interface CostWithCurrencyViewUiBinder extends UiBinder<HTMLPanel, MoneyCurrencyWidget> {}
    private static CostWithCurrencyViewUiBinder ourUiBinder = GWT.create(CostWithCurrencyViewUiBinder.class);
}
