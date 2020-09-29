package ru.protei.portal.ui.common.client.activity.externallink;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;

public abstract class ExternalLinkActivity implements Activity {

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        fireEvent(new AppEvents.InitExternalLinks());
    }
}
