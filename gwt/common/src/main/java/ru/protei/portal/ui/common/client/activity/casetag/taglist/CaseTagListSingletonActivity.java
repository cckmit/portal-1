package ru.protei.portal.ui.common.client.activity.casetag.taglist;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.CaseTagEvents;

public abstract class CaseTagListSingletonActivity implements Activity {

    @Event
    public void onShow(CaseTagEvents.ShowList event) {
        CaseTagListActivity activity = activityProvider.get();
        activity.onShow(event);
        if (event.tagListActivityConsumer != null) {
            event.tagListActivityConsumer.accept(activity);
        }
    }

    @Inject
    Provider<CaseTagListActivity> activityProvider;
}
