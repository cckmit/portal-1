package ru.protei.portal.ui.common.client.widget.selector.report.timeelapsedgroup;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_TimeElapsedGroup;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;

import java.util.Arrays;

public abstract class TimeElapsedGroupModel extends BaseSelectorModel<En_TimeElapsedGroup> implements Activity {
    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        clean();
        load();
    }

    private void load() {
        updateElements(Arrays.asList(En_TimeElapsedGroup.values()));
    }
}
