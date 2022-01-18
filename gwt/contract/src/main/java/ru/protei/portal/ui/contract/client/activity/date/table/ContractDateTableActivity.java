package ru.protei.portal.ui.contract.client.activity.date.table;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.core.model.struct.Money;
import ru.protei.portal.ui.common.client.events.ContractDateEvents;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.List;
import java.util.Objects;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;


public abstract class ContractDateTableActivity implements AbstractContractDateTableActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInit(ContractDateEvents.Init event) {
        this.init = event;
    }

    @Event
    public void onShow(ContractDateEvents.ShowTable event) {
        this.showTable = event;

        event.parent.clear();
        event.parent.add(view.asWidget());

        view.setData(event.contractDates);

        view.hideWarning();
        showCostOverflowWarning(showTable.contractDates);
    }

    @Event
    public void onRefreshClicked(ContractDateEvents.Refresh event) {
        onRefreshDates();
    }

    @Event
    public void contractDateAdded(ContractDateEvents.Added event) {
        view.addRow(event.value);
        onRefreshDates();
    }

    @Override
    public void onEditClicked(ContractDate value) {
        fireEvent(new ContractDateEvents.ShowEdit(value));
    }

    @Override
    public void onRemoveClicked(ContractDate value) {
        if (value == null) {
            return;
        }

        showTable.contractDates.remove(value);
        view.removeRow(value);

        showCostOverflowWarning(showTable.contractDates);
    }

    private void onRefreshDates() {
        view.setData(showTable.contractDates);
        showCostOverflowWarning(showTable.contractDates);
    }

    private void showCostOverflowWarning(List<ContractDate> value) {
        long costOfPayments = stream(value)
                .map(ContractDate::getCost)
                .filter(Objects::nonNull)
                .mapToLong(Money::getFull)
                .sum();
        long costOfContract = init.contractCostSupplier.get().getFull();
        if (costOfContract == costOfPayments) {
            view.hideWarning();
            return;
        }

        view.showWarning(costOfPayments > costOfContract ? lang.contractDatesWarningCostOverflow() : lang.contractDatesWarningCostFlow());
    }

    @Inject
    Lang lang;
    @Inject
    AbstractContractDateTableView view;

    private ContractDateEvents.Init init;
    private ContractDateEvents.ShowTable showTable;
}
