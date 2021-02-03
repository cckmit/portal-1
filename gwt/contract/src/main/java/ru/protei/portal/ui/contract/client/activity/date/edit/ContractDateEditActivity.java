package ru.protei.portal.ui.contract.client.activity.date.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_ContractDatesType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.core.model.struct.Money;
import ru.protei.portal.core.model.struct.MoneyWithCurrency;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.ContractDateEvents;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.function.Consumer;


public abstract class ContractDateEditActivity implements Activity,
        AbstractContractDateEditActivity, AbstractDialogDetailsActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        dialogView.setActivity(this);
        dialogView.getBodyContainer().add(view.asWidget());
        dialogView.setHeader(lang.contractDateEditHeader());
    }

    @Event
    public void onInit(ContractDateEvents.Init event) {
        this.init = event;
        view.setCostChangeListener(money -> view.moneyPercent().setValue(calculatePercent(money)));
    }

    @Event
    public void onShow(ContractDateEvents.ShowEdit event) {
        this.value = event.value;
        boolean isAllowedEdit = policyService.hasPrivilegeFor(En_Privilege.CONTRACT_EDIT);

        if (value == null) {
            value = new ContractDate();
        }
        fillView(value);

        dialogView.removeButtonVisibility().setVisible(false);
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
            return;
        }
    }

    @Override
    public void onCancelClicked() {
        dialogView.hidePopup();
    }

    private void fillView(ContractDate value) {
        view.type().setValue(value.getType());
        view.date().setValue(value.getDate());
        view.costType().setValue(value.getCostType());
        view.notifyFlag().setValue(value.isNotify());
        view.notifyFlagEnabled().setEnabled(value.getDate() != null);
        view.comment().setValue(value.getComment());
        view.setMoneyValidationFunction(cost -> {
            boolean isCostEnabled = isTypeWithPayment(value.getType());
            if (isCostEnabled) {
                return cost != null && cost.getFull() >= 0;
            } else {
                return cost == null;
            }
        });
        view.moneyWithCurrency().setValue(new MoneyWithCurrency(value.getCost(), value.getCurrency()));
        view.moneyWithCurrencyEnabled().setEnabled(isTypeWithPayment(value.getType()));
        view.moneyPercent().setValue(calculatePercent(value.getCost()));
        view.moneyPercentEnabled().setEnabled(isTypeWithPayment(value.getType()));
        view.calendarDaysEnabled().setEnabled(init.dateSignedSupplier.get() != null);
    }
    private boolean validate() {
        return true;
    }

    private boolean isNew( ContractDate value ) {
        return value!=null && value.getId() == null;
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

    private ContractDate value;
    private ContractDateEvents.Init init;
}
