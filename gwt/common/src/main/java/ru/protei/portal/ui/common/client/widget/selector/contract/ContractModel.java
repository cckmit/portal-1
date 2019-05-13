package ru.protei.portal.ui.common.client.widget.selector.contract;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.ContractEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.LifecycleSelectorModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ContractModel extends LifecycleSelectorModel<EntityOption> {

    @Event
    public void onInit(AuthEvents.Success event) {
        clear();
    }

    @Event
    public void onCompanyListChanged(ContractEvents.ChangeModel event) {
        refreshOptions();
    }

    @Override
    protected void refreshOptions() {
        contractController.getContracts(new ContractQuery(), new FluentCallback<List<Contract>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess((contracts, m) -> {
                    notifySubscribers(contracts.stream()
                            .map(Contract::toEntityOption)
                            .collect(Collectors.toList()));
                })
        );
    }

    @Inject
    Lang lang;
    @Inject
    ContractControllerAsync contractController;

}
