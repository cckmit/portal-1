package ru.protei.portal.ui.contract.client.activity.date.table;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.ui.common.client.events.ContractDateEvents;
import ru.protei.portal.ui.common.client.lang.Lang;


public abstract class ContractDateTableActivity implements AbstractContractDateTableActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(ContractDateEvents.ShowTable event) {
        this.event  = event;

        event.parent.clear();
        event.parent.add(view.asWidget());

        view.setData(event.contractDates);
    }

    @Event
    public void onRefreshClicked(ContractDateEvents.Refresh event) {
        view.refresh();
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

        event.contractDates.remove(value);
        view.removeRow(value);
    }

    @Inject
    Lang lang;
    @Inject
    AbstractContractDateTableView view;

    private ContractDateEvents.ShowTable event;
}
