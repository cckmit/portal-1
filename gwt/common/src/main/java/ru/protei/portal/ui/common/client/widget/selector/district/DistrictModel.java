package ru.protei.portal.ui.common.client.widget.selector.district;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.struct.DistrictInfo;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.LifecycleSelectorModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

/**
 * Модель статусов
 */
public abstract class DistrictModel extends LifecycleSelectorModel<DistrictInfo> {

    @Event
    public void onInit( AuthEvents.Success event ) {
        clear();
    }

    @Event
    public void onStateListChanged(IssueEvents.ChangeStateModel event) {
        refreshOptions();
    }

    @Override
    protected void refreshOptions() {
        regionService.getDistrictList(new FluentCallback<List<DistrictInfo>>()
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
