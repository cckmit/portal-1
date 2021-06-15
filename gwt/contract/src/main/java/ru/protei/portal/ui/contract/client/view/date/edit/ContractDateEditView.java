package ru.protei.portal.ui.contract.client.view.date.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.dict.ContractCostType;
import ru.protei.portal.core.model.dict.En_ContractDatesType;
import ru.protei.portal.core.model.struct.Money;
import ru.protei.portal.core.model.struct.MoneyWithCurrency;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.ContractCostTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;
import ru.protei.portal.ui.common.client.widget.money.MoneyCurrencyWidget;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableDoubleBox;
import ru.protei.portal.ui.contract.client.activity.date.edit.AbstractContractDateEditActivity;
import ru.protei.portal.ui.contract.client.activity.date.edit.AbstractContractDateEditView;
import ru.protei.portal.ui.contract.client.widget.selector.ContractDatesTypeSelector;

import java.util.Date;
import java.util.function.Function;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.DISABLED;

public class ContractDateEditView extends Composite implements AbstractContractDateEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        costType.setModel(elementIndex -> {
            ContractCostType[] list = ContractCostType.values();
            if (list.length <= elementIndex) return null;
            return list[elementIndex];
        });
        costType.setItemRenderer(value -> costTypeLang.getName(value));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractContractDateEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<En_ContractDatesType> type() {
        return type;
    }

    @Override
    public HasValue<Date> date() {
        return date;
    }

    @Override
    public HasValue<Boolean> notifyFlag() {
        return notify;
    }

    @Override
    public HasEnabled notifyFlagEnabled() {
        return notify;
    }

    @Override
    public HasValue<String> comment() {
        return comment;
    }

    @Override
    public HasValue<MoneyWithCurrency> moneyWithCurrency() {
        return moneyWithCurrency;
    }

    @Override
    public HasValue<Double> moneyPercent() {
        return moneyPercent;
    }

    @Override
    public void setMoneyFieldsEnabled(boolean isEnabled) {
        moneyPercent.setEnabled(isEnabled);
        moneyWithCurrency.setEnabled(isEnabled);
        setEnabledContainer(moneyPercentContainer, isEnabled);
        setEnabledContainer(moneyWithCurrencyContainer, isEnabled);
    }

    @Override
    public void setMoneyValidationFunction(Function<Money, Boolean> validationFunction) {
        moneyWithCurrency.setMoneyValidationFunction(validationFunction);
    }

    @Override
    public HasValue<ContractCostType> costType() {
        return costType;
    }

    @Override
    public HasValue<Long> calendarDays() {
        return calendarDay;
    }

    @Override
    public void setCalendarDaysEnabled(boolean isEnabled) {
        calendarDay.setEnabled(isEnabled);
        setEnabledContainer(calendarDayContainer, isEnabled);
    }

    @UiHandler("calendarDay")
    public void onCalendarDaysChanged(ValueChangeEvent<Long> event) {
        activity.onCalendarDaysChanged();
    }

    @UiHandler("type")
    public void onTypeChanged(ValueChangeEvent<En_ContractDatesType> event) {
        activity.onTypeChanged();
    }

    @UiHandler("moneyPercent")
    public void onPercentChanged(ValueChangeEvent<Double> event) {
        activity.onPercentChanged();
    }

    @UiHandler("moneyWithCurrency")
    public void onCostChanged(ValueChangeEvent<MoneyWithCurrency> event) {
        activity.onCostChanged();
    }

    @UiHandler("date")
    public void onDateChanged(ValueChangeEvent<Date> event) {
        activity.onDateChanged();
    }

    private void setEnabledContainer(DivElement container, boolean isEnabled) {
        if (isEnabled) {
            container.removeClassName(DISABLED);
            return;
        }
        container.addClassName(DISABLED);
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        root.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.DATE_ITEM.ITEM);
        type.setEnsureDebugId(DebugIds.CONTRACT.DATE_ITEM.TYPE_BUTTON);
        date.getElement().getFirstChildElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.DATE_ITEM.DATE_CONTAINER);
        comment.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.DATE_ITEM.COMMENT_INPUT);
        notify.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.DATE_ITEM.NOTIFY_SWITCHER);
        calendarDay.ensureDebugId(DebugIds.CONTRACT.DATE_ITEM.CALENDAR_DAY);
        costType.ensureDebugId(DebugIds.CONTRACT.PAYMENT.COST_TYPE_SELECTOR);
        moneyPercent.ensureDebugId(DebugIds.CONTRACT.PAYMENT.MONEY_PERCENT_INPUT);
    }

    @UiField
    TextBox comment;
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
    @UiField
    LongBox calendarDay;
    @UiField
    FormPopupSingleSelector<ContractCostType> costType;
    @UiField
    DivElement moneyPercentContainer;
    @UiField
    DivElement moneyWithCurrencyContainer;
    @UiField
    DivElement calendarDayContainer;

    @Inject
    ContractCostTypeLang costTypeLang;

    private AbstractContractDateEditActivity activity;

    interface ContractDateEditViewUiBinder extends UiBinder<Widget, ContractDateEditView> {}
    private static ContractDateEditViewUiBinder ourUiBinder = GWT.create(ContractDateEditViewUiBinder.class);
}
