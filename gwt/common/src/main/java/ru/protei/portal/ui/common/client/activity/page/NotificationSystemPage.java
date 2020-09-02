package ru.protei.portal.ui.common.client.activity.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;

import static ru.protei.portal.ui.common.client.activity.page.util.AccessUtil.canUseExternalLink;

public abstract class NotificationSystemPage implements Activity {

    @PostConstruct
    public void onInit() {
        TAB = lang.notificationSystem();
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        if (canUseExternalLink(event.profile)) {
            fireEvent(new MenuEvents.Add(TAB, UiConstants.TabIcons.NOTIFICATION_SYSTEM, TAB, NOTIFICATION_SYSTEM_URL, DebugIds.SIDEBAR_MENU.NOTIFICATION_SYSTEM));
        }
    }

    @Inject
    Lang lang;

    private String TAB;
    private final static String NOTIFICATION_SYSTEM_URL = "https://oldportal.protei.ru/ns/index.jsp";
}
