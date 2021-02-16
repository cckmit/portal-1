package ru.protei.portal.ui.common.client.widget.selector.contract;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.ContractEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.LifecycleSelectorModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ContractModel extends BaseSelectorModel<EntityOption> implements Activity {

    @Event
    public void onInit(AuthEvents.Success event) {
        clean();
    }

    @Event
    public void onContractListChanged(ContractEvents.ChangeModel event) {
        clean();
    }

    @Override
    protected void requestData(LoadingHandler selector, String searchText ) {
        contractController.getContracts(new ContractQuery(), new FluentCallback<SearchResult<Contract>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(contracts -> {
                    List<EntityOption> options = contracts.getResults().stream()
                            .map(Contract::toEntityOption)
                            .collect(Collectors.toList());
                    updateElements(options, selector);
                })
        );
    }

    @Inject
    Lang lang;
    @Inject
    ContractControllerAsync contractController;

}
