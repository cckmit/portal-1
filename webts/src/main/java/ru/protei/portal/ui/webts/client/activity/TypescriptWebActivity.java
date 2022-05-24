package ru.protei.portal.ui.webts.client.activity;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.webts.client.integration.NativeWebIntegration;
import ru.protei.portal.ui.webts.client.model.TypescriptWebGwtEvents;
import ru.protei.portal.ui.webts.client.model.event.EventBusEventAppNotificationRequest;
import ru.protei.portal.ui.webts.client.model.event.EventBusEventAuthLoginDone;
import ru.protei.portal.ui.webts.client.model.event.EventBusEventAuthLogoutDone;

public abstract class TypescriptWebActivity implements Activity {

    @Event
    public void onTypescriptWebInit(TypescriptWebGwtEvents.Init ev) {
        nativeWebIntegration.setup();
        listenAppNotifications();
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

    private void listenAppNotifications() {
        nativeWebIntegration.<EventBusEventAppNotificationRequest>listenEvent(EventBusEventAppNotificationRequest.type, (event) -> {
            String notificationId = event.getNotificationId();
            String notificationType = event.getNotificationType();
            String notificationMessage = event.getNotificationMessage();
            NotifyEvents.NotifyType type = makeNotifyType(notificationType);
            fireEvent(new NotifyEvents.Show(notificationMessage, type));
        });
    }

    private NotifyEvents.NotifyType makeNotifyType(String type) {
        switch (type) {
            case "warn": return NotifyEvents.NotifyType.ERROR;
            case "error": return NotifyEvents.NotifyType.ERROR;
            case "success": return NotifyEvents.NotifyType.SUCCESS;
        }
        return NotifyEvents.NotifyType.INFO;
    }

    @Inject
    private NativeWebIntegration nativeWebIntegration;
}
