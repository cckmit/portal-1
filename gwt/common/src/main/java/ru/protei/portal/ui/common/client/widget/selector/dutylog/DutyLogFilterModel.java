package ru.protei.portal.ui.common.client.widget.selector.dutylog;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.ui.common.client.events.DutyLogFilterEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.DutyLogFilterControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class DutyLogFilterModel extends BaseSelectorModel<FilterShortView> implements Activity {
    @Event
    public void onDutyLogFilterChange(DutyLogFilterEvents.Changed event) {
        clean();
    }

    @Override
    protected void requestData(LoadingHandler selector, String searchText) {
        controller.getShortViewList(new FluentCallback<List<FilterShortView>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(list -> {
                    updateElements(list, selector);
                })
        );
    }

    @Inject
    DutyLogFilterControllerAsync controller;
    @Inject
    Lang lang;
}