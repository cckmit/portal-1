package ru.protei.portal.ui.common.client.widget.selector.contractor.multicontractor;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.ui.common.client.events.ContractEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class MultiContractorModel extends BaseSelectorModel<Contractor> implements Activity {

    @Event
    public void onContractListChanged(ContractEvents.ChangeModel event) {
        clean();
    }

    @Override
    protected void requestData(LoadingHandler selector, String searchText) {
        controller.getContractorList(new FluentCallback<List<Contractor>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(list -> {
                    updateElements( list, selector );
                })
        );
    }

    @Inject
    ContractControllerAsync controller;
    @Inject
    Lang lang;
}
