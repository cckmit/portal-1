package ru.protei.portal.ui.contract.client.view.date.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

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
        MoneyWithCurrency mwc = moneyWithCurrency.getValue();
        Money cost = mwc != null ? mwc.getMoney() : null;

        if (onCostChanged != null) {
            onCostChanged.accept(cost);
        }
        return moneyWithCurrency;
    }

    @Override
    public HasEnabled moneyWithCurrencyEnabled() {
        return moneyWithCurrency;
    }

    @Override
    public HasValue<Double> moneyPercent() {
        return moneyPercent;
    }

    @Override
    public HasEnabled moneyPercentEnabled() {
        return moneyPercent;
    }

    @Override
    public void setCostChangeListener(Consumer<Money> onCostChanged) {
        this.onCostChanged = onCostChanged;
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
    public HasEnabled calendarDaysEnabled() {
        return calendarDay;
    }

    private void ensureDebugIds() {
        root.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.DATE_ITEM.ITEM);
        type.setEnsureDebugId(DebugIds.CONTRACT.DATE_ITEM.TYPE_BUTTON);
        date.getElement().getFirstChildElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.DATE_ITEM.DATE_CONTAINER);
        comment.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.DATE_ITEM.COMMENT_INPUT);
        notify.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.DATE_ITEM.NOTIFY_SWITCHER);
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
    IntegerBox calendarDay;
    @UiField
    FormPopupSingleSelector<ContractCostType> costType;

    @Inject
    ContractCostTypeLang costTypeLang;

    private Consumer<Money> onCostChanged;
    private AbstractContractDateEditActivity activity;

    interface ContractDateEditViewUiBinder extends UiBinder<Widget, ContractDateEditView> {}
    private static ContractDateEditViewUiBinder ourUiBinder = GWT.create(ContractDateEditViewUiBinder.class);
}
