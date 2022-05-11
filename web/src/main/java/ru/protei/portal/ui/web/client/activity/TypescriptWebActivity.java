package ru.protei.portal.ui.web.client.activity;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.web.client.integration.NativeWebIntegration;
import ru.protei.portal.ui.web.client.model.TypescriptWebGwtEvents;
import ru.protei.portal.ui.web.client.model.event.EventBusEventAuthLoginDone;
import ru.protei.portal.ui.web.client.model.event.EventBusEventAuthLogoutDone;

public abstract class TypescriptWebActivity implements Activity {

    @Event
    public void onTypescriptWebInit(TypescriptWebGwtEvents.Init ev) {
        nativeWebIntegration.setup();
    }

    @Event
    public void onAuthLogin(AuthEvents.Success event) {
        Long personId = event.profile.getId();
        Long loginId = event.profile.getLoginId();
        nativeWebIntegration.fireEvent(EventBusEventAuthLoginDone.create(personId, loginId));
    }

    @Event
    public void onAuthLogout(AppEvents.Logout event) {
        nativeWebIntegration.fireEvent(EventBusEventAuthLogoutDone.create(true));
    }

    @Inject
    private NativeWebIntegration nativeWebIntegration;
}
