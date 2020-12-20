package ru.protei.portal.ui.common.client.activity.externallink;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.service.AppServiceAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

public abstract class ExternalLinkActivity implements Activity {

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        appService.getExternalLinksHtml(new FluentCallback<String>()
                .withSuccess(s -> fireEvent(new AppEvents.InitExternalLinks(s))));
    }

    @Inject
    AppServiceAsync appService;
}
