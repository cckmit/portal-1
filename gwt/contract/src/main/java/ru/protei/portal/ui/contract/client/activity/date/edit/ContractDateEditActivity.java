package ru.protei.portal.ui.contract.client.activity.date.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_ContractDatesType;
import ru.protei.portal.core.model.dict.En_Currency;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.core.model.struct.Money;
import ru.protei.portal.core.model.struct.MoneyWithCurrency;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.ContractDateEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.util.DateUtils;


public abstract class ContractDateEditActivity implements Activity,
        AbstractContractDateEditActivity, AbstractDialogDetailsActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.setMoneyValidationFunction(cost -> {
            boolean isCostEnabled = isTypeWithPayment(view.type().getValue());
            if (isCostEnabled) {
                return cost != null && cost.getFull() >= 0;
            } else {
                return cost == null;
            }
        });
        dialogView.setActivity(this);
        dialogView.getBodyContainer().add(view.asWidget());
        dialogView.setHeader(lang.contractDateEditHeader());
        dialogView.removeButtonVisibility().setVisible(false);
    }

    @Event
    public void onInit(ContractDateEvents.Init event) {
        this.init = event;
    }

    @Event
    public void onShow(ContractDateEvents.ShowEdit event) {
        isNew = event.value == null;
        value = isNew ? new ContractDate() : event.value;

        boolean isAllowedEdit = policyService.hasPrivilegeFor(En_Privilege.CONTRACT_EDIT);
        fillView();
        dialogView.saveButtonVisibility().setVisible(isAllowedEdit);
        dialogView.showPopup();
    }

    @Override
    public void onRemoveClicked() {
        fireEvent(new ContractDateEvents.Removed(value));
    }

    @Override
    public void onSaveClicked() {
        if (!validate()) {
            fireEvent(new NotifyEvents.Show(lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        fillDto();
        dialogView.hidePopup();
        if (isNew) {
            fireEvent(new ContractDateEvents.Added(value));
            return;
        }
        fireEvent(new ContractDateEvents.Refresh());
    }

    @Override
    public void onCancelClicked() {
        dialogView.hidePopup();
    }

    @Override
    public void onDateChanged() {
        view.calendarDays().setValue(DateUtils.getDaysBetween(init.dateSignedSupplier.get(), view.date().getValue()));
        checkNotifyFlagState();
    }

    @Override
    public void onCalendarDaysChanged() {
        view.date().setValue(DateUtils.addDays(init.dateSignedSupplier.get(), view.calendarDays().getValue()));
        checkNotifyFlagState();
    }

    @Override
    public void onPercentChanged() {
        Money cost = calculateCost(view.moneyPercent().getValue());
        MoneyWithCurrency mwc = view.moneyWithCurrency().getValue();
        mwc.setMoney(cost);
        view.moneyWithCurrency().setValue(mwc);
    }

    @Override
    public void onCostChanged() {
        MoneyWithCurrency mwc = view.moneyWithCurrency().getValue();
        Money cost = mwc != null ? mwc.getMoney() : null;
        view.moneyPercent().setValue(calculatePercent(cost));
    }

    @Override
    public void onTypeChanged() {
        view.setMoneyFieldsEnabled(isTypeWithPayment(view.type().getValue()));
    }

    private void fillView() {
        view.type().setValue(value.getType());
        view.date().setValue(value. getDate());
        view.costType().setValue(value.getCostType());
        view.notifyFlag().setValue(value.isNotify());
        view.notifyFlagEnabled().setEnabled(value.getDate() != null);
        view.comment().setValue(value.getComment());

        view.setMoneyFieldsEnabled(isTypeWithPayment(value.getType()));
        view.moneyWithCurrency().setValue(new MoneyWithCurrency(value.getCost(), value.getCurrency()));
        view.moneyPercent().setValue(calculatePercent(value.getCost()));
        view.setCalendarDaysEnabled(init.dateSignedSupplier.get() != null);
        view.calendarDays().setValue(DateUtils.getDaysBetween(init.dateSignedSupplier.get(), value.getDate()));
    }

    private void fillDto() {
        value.setType(view.type().getValue());
        value.setDate(view.date().getValue());
        value.setCostType(view.costType().getValue());
        value.setNotify(view.notifyFlag().getValue());
        value.setComment(view.comment().getValue());

        MoneyWithCurrency mwc = view.moneyWithCurrency().getValue();
        Money cost = mwc != null ? mwc.getMoney() : null;
        En_Currency currency = mwc != null ? mwc.getCurrency() : null;
        value.setCost(cost);
        value.setCurrency(currency);
    }

    private boolean validate() {
        if ( view.type().getValue() != null ) {
            return true;
        }
        return false;
    }

    private void checkNotifyFlagState() {
        boolean isDatePresent = view.date().getValue() != null;
        view.notifyFlagEnabled().setEnabled(isDatePresent);
        if (!isDatePresent) {
            view.notifyFlag().setValue(false);
        }
    }

    private boolean isTypeWithPayment(En_ContractDatesType type) {
        return type == En_ContractDatesType.PREPAYMENT ||
                type == En_ContractDatesType.POSTPAYMENT;
    }

    private Double calculatePercent(Money cost) {
        if (cost == null) {
            return null;
        }
        double ratio = (double) cost.getFull() / (double) init.contractCostSupplier.get().getFull();
        double percent = ratio * 100;
        return percent;
    }

    private Money calculateCost(Double percent) {
        if (percent == null) {
            return null;
        }
        double ratio = percent / 100;
        long cost = (long) ((double) init.contractCostSupplier.get().getFull() * ratio);
        return new Money(cost);
    }

    @Inject
    Lang lang;
    @Inject
    AbstractContractDateEditView view;
    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    PolicyService policyService;

    private boolean isNew = false;
    private ContractDate value;
    private ContractDateEvents.Init init;
}
