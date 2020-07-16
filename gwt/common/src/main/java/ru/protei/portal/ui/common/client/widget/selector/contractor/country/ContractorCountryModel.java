package ru.protei.portal.ui.common.client.widget.selector.contractor.country;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.ent.ContractorCountry;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class ContractorCountryModel extends BaseSelectorModel<ContractorCountry> implements Activity {

    @Override
    protected void requestData(LoadingHandler selector, String searchText) {
        controller.getContractorCountryList(organization, new FluentCallback<List<ContractorCountry>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(list -> {
                    updateElements(list, selector);
                })
        );
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @Inject
    ContractControllerAsync controller;
    @Inject
    Lang lang;

    String organization;
}
