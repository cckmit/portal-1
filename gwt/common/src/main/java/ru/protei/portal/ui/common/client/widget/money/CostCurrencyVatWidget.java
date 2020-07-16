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
import ru.protei.portal.core.model.struct.CostWithCurrencyWithVat;
import ru.protei.portal.ui.common.client.widget.selector.currency.CurrencyButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.vat.VatButtonSelector;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.helper.NumberUtils.parseLong;

public class CostCurrencyVatWidget extends Composite implements HasValue<CostWithCurrencyWithVat>, HasEnabled {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        setVatOptions(defaultVatOptions);
    }

    @Override
    public CostWithCurrencyWithVat getValue() {
        return new CostWithCurrencyWithVat(cost.getValue(), currency.getValue(), vat.getValue());
    }

    @Override
    public void setValue(CostWithCurrencyWithVat value) {
        setValue(value, false);
    }

    @Override
    public void setValue(CostWithCurrencyWithVat value, boolean fireEvents) {
        cost.setValue(value.getCost());
        currency.setValue(value.getCurrency() == null ? defaultCurrency : value.getCurrency());
        vat.setValue(value.getVatPercent());
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public boolean isEnabled() {
        return cost.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        cost.setEnabled(enabled);
        currency.setEnabled(enabled);
        vat.setEnabled(enabled);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<CostWithCurrencyWithVat> handler) {
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

    private Long parseVat(String vat) {
        return Objects.equals(vat, "no")
                ? null
                : parseLong(vat);
    }

    @UiHandler("cost")
    public void onCostChanged(ValueChangeEvent<Long> event) {
        CostWithCurrencyWithVat value = getValue();
        ValueChangeEvent.fire(this, value);
    }

    @UiHandler("currency")
    public void onCurrencyChanged(ValueChangeEvent<En_Currency> event) {
        CostWithCurrencyWithVat value = getValue();
        ValueChangeEvent.fire(this, value);
    }

    @UiHandler("vat")
    public void onVatChanged(ValueChangeEvent<Long> event) {
        CostWithCurrencyWithVat value = getValue();
        ValueChangeEvent.fire(this, value);
    }

    @UiField
    LongBox cost;
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

    interface CostCurrencyVatWidgetUiBinder extends UiBinder<HTMLPanel, CostCurrencyVatWidget> {}
    private static CostCurrencyVatWidgetUiBinder ourUiBinder = GWT.create(CostCurrencyVatWidgetUiBinder.class);
}
