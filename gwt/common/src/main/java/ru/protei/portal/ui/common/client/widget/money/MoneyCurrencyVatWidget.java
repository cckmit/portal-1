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
import ru.protei.portal.core.model.struct.MoneyWithCurrencyWithVat;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.widget.selector.currency.CurrencyButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.vat.VatButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableMoneyBox;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.helper.NumberUtils.parseLong;

public class MoneyCurrencyVatWidget extends Composite implements HasValue<MoneyWithCurrencyWithVat>, HasEnabled {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initValidation();
        setVatOptions(defaultVatOptions);
        ensureDebugIds();
    }

    @Override
    public MoneyWithCurrencyWithVat getValue() {
        Money vMoney = money.getValue();
        En_Currency vCurrency = currency.getValue();
        Long vVat = vat.getValue();
        return new MoneyWithCurrencyWithVat(vMoney, vCurrency, vVat);
    }

    @Override
    public void setValue(MoneyWithCurrencyWithVat value) {
        setValue(value, false);
    }

    @Override
    public void setValue(MoneyWithCurrencyWithVat value, boolean fireEvents) {
        Money vMoney = value.getMoney();
        En_Currency vCurrency = value.getCurrency() != null
                ? value.getCurrency()
                : defaultCurrency;
        money.setValue(vMoney);
        currency.setValue(vCurrency);
        vat.setValue(value.getVatPercent());
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public boolean isEnabled() {
        return money.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        money.setEnabled(enabled);
        currency.setEnabled(enabled);
        vat.setEnabled(enabled);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<MoneyWithCurrencyWithVat> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void setDefaultCurrency(En_Currency value) {
        defaultCurrency = value;
        currency.setValue(value);
    }

    public void setEnsureDebugId(String debugId) {
        root.ensureDebugId(debugId);
    }

    public void setVatOptions(String options) {
        setVatOptions(stream(Arrays.asList(options.split(",")))
                .map(this::parseVat)
                .collect(Collectors.toList()));
    }

    public void setVatOptions(List<Long> options) {
        vat.setOptions(options);
    }

    private void initValidation() {
        money.setValidationFunction(value -> value != null && value.getFull() >= 0);
    }

    private Long parseVat(String vat) {
        return Objects.equals(vat, "no")
                ? null
                : parseLong(vat);
    }

    @UiHandler("money")
    public void onMoneyChanged(ValueChangeEvent<Money> event) {
        MoneyWithCurrencyWithVat value = getValue();
        ValueChangeEvent.fire(this, value);
    }

    @UiHandler("currency")
    public void onCurrencyChanged(ValueChangeEvent<En_Currency> event) {
        MoneyWithCurrencyWithVat value = getValue();
        ValueChangeEvent.fire(this, value);
    }

    @UiHandler("vat")
    public void onVatChanged(ValueChangeEvent<Long> event) {
        MoneyWithCurrencyWithVat value = getValue();
        ValueChangeEvent.fire(this, value);
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        currency.setEnsureDebugId(DebugIds.CONTRACT.CURRENCY_SELECTOR);
        vat.setEnsureDebugId(DebugIds.CONTRACT.VAT_SELECTOR);
    }

    @UiField
    ValidableMoneyBox money;
    @Inject
    @UiField(provided = true)
    CurrencyButtonSelector currency;
    @Inject
    @UiField(provided = true)
    VatButtonSelector vat;
    @UiField
    HTMLPanel root;

    private En_Currency defaultCurrency;

    private static final List<Long> defaultVatOptions = Arrays.asList(20L, 0L, null);

    interface CostCurrencyVatWidgetUiBinder extends UiBinder<HTMLPanel, MoneyCurrencyVatWidget> {}
    private static CostCurrencyVatWidgetUiBinder ourUiBinder = GWT.create(CostCurrencyVatWidgetUiBinder.class);
}
