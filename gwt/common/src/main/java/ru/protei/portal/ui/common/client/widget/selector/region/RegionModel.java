package ru.protei.portal.ui.common.client.widget.selector.region;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.LifecycleSelectorModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

/**
 * Модель регионов
 */
public abstract class RegionModel extends LifecycleSelectorModel<EntityOption> {

    @Event
    public void onInit( AuthEvents.Success event ) {
        clear();
    }

    @Override
    protected void refreshOptions() {
        regionService.getRegionList(new FluentCallback<List<EntityOption>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(this::notifySubscribers));
    }

    @Inject
    RegionControllerAsync regionService;
    @Inject
    Lang lang;
}
