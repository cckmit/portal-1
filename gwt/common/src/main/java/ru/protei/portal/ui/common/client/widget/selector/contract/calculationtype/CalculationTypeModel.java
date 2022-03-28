package ru.protei.portal.ui.common.client.widget.selector.contract.calculationtype;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.ent.CalculationType;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

import static ru.protei.portal.core.model.dict.En_ResultStatus.ORGANIZATION_REQUIRED;

public abstract class CalculationTypeModel extends BaseSelectorModel<CalculationType> implements Activity {

    @Override
    protected void requestData(LoadingHandler selector, String searchText) {
        controller.getCalculationTypeList(organization, new FluentCallback<List<CalculationType>>()
                .withError((throwable, defaultErrorHandler, status) -> {
                    String errorMessage = status.equals(ORGANIZATION_REQUIRED) ? lang.errOrganizationRequired()
                                                                               : lang.errGetList();
                    fireEvent(new NotifyEvents.Show(errorMessage, NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(list -> updateElements(list, selector))
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
