package ru.protei.portal.ui.common.client.activity.casetag;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.activity.casetag.list.AbstractCaseTagListActivity;
import ru.protei.portal.ui.common.client.events.CaseTagEvents;

public abstract class CaseTagActivity implements Activity {

    @Event
    public void onShow(CaseTagEvents.Show event) {
        activityProvider.get().onShow(event);
    }

    @Inject
    Provider<AbstractCaseTagListActivity> activityProvider;
}
