package ru.protei.portal.ui.common.client.widget.selector.report.youtrackwork;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_ReportYoutrackWorkType;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;

import java.util.Arrays;

public abstract class ReportYoutrackDictionaryModel extends BaseSelectorModel<En_ReportYoutrackWorkType> implements Activity {
    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        clean();
        load();
    }

    private void load() {
        updateElements(Arrays.asList(En_ReportYoutrackWorkType.NIOKR, En_ReportYoutrackWorkType.NMA));
    }
}
