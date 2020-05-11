package ru.protei.portal.ui.roomreservation.client.widget.selector.month;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;

import static ru.protei.portal.ui.roomreservation.client.util.DateUtils.makeAvailableMonths;

public abstract class MonthModel extends BaseSelectorModel<Integer> implements Activity {

    @Event
    public void onInit(AuthEvents.Success event) {
        clean();
        load();
    }

    private void load() {
        updateElements(makeAvailableMonths());
    }
}
