package ru.protei.portal.ui.common.client.widget.selector.contract.calculationtype;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.ent.ContractCalculationType;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

import static ru.protei.portal.core.model.util.CrmConstants.Company.MAIN_HOME_COMPANY_NAME;

public abstract class ContractCalculationTypeModel extends BaseSelectorModel<ContractCalculationType> implements Activity {

    @Override
    protected void requestData(LoadingHandler selector, String searchText) {
        controller.getCalculationTypeList(organization, new FluentCallback<List<ContractCalculationType>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(list -> {
                    updateElements(list, selector);
                })
        );
    }

    private String organization;

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @Inject
    ContractControllerAsync controller;
    @Inject
    Lang lang;
}
