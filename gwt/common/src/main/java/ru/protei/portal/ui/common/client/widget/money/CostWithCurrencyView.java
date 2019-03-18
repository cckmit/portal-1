package ru.protei.portal.ui.common.client.widget.money;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Currency;
import ru.protei.portal.core.model.struct.CostWithCurrency;
import ru.protei.portal.ui.common.client.widget.selector.currency.CurrencyButtonSelector;

public class CostWithCurrencyView extends Composite implements HasValue<CostWithCurrency>, HasEnabled {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public CostWithCurrency getValue() {
        return new CostWithCurrency(cost.getValue(), currency.getValue());
    }

    @Override
    public void setValue(CostWithCurrency value) {
        setValue(value, false);
    }

    @Override
    public void setValue(CostWithCurrency value, boolean fireEvents) {
        updateUI(value);
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        cost.setEnabled(enabled);
        currency.setEnabled(enabled);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<CostWithCurrency> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void setDefaultCost(Long value) {
        cost.setValue(value);
    }

    public void setDefaultCurrency(En_Currency value) {
        defaultCurrency = value;
        currency.setValue(value);
    }

    private void updateUI(CostWithCurrency value) {
        cost.setValue(value.getCost());
        currency.setValue(value.getCurrency() == null ? defaultCurrency : value.getCurrency());
    }

    @UiField
    LongBox cost;
    @Inject
    @UiField(provided = true)
    CurrencyButtonSelector currency;

    private En_Currency defaultCurrency;
    private boolean enabled = true;

    interface CostWithCurrencyViewUiBinder extends UiBinder<HTMLPanel, CostWithCurrencyView> {}
    private static CostWithCurrencyViewUiBinder ourUiBinder = GWT.create(CostWithCurrencyViewUiBinder.class);
}
