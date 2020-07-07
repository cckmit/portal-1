package ru.protei.portal.ui.common.client.widget.selector.contractor.filtercontractor;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.LifecycleSelectorModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class MultiContractorModel extends LifecycleSelectorModel<Contractor> implements Activity {

    @Override
    protected void refreshOptions() {
        controller.getContractorList(new FluentCallback<List<Contractor>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(list -> {
                    notifySubscribers(list);
                })
        );
    }

    @Inject
    ContractControllerAsync controller;
    @Inject
    Lang lang;
}
