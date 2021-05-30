package ru.protei.portal.ui.common.client.widget.selector.delivery.filter;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.ui.common.client.events.DeliveryEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.CaseFilterControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class DeliveryFilterSelectorModel extends BaseSelectorModel<FilterShortView> implements Activity {
    @Event
    public void onDeliveryFilterChanged(DeliveryEvents.ChangeUserFilterModel event) {
        clean();
    }

    @Override
    protected void requestData(LoadingHandler selector, String searchText) {
        controller.getCaseFilterShortViewList(En_CaseFilterType.DELIVERY, new FluentCallback<List<FilterShortView>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(list -> updateElements(list, selector))
        );
    }

    @Inject
    CaseFilterControllerAsync controller;
    @Inject
    Lang lang;
}
