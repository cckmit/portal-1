package ru.protei.portal.ui.common.client.activity.page;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.winter.web.common.client.events.MenuEvents;

import static ru.protei.portal.ui.common.client.activity.page.util.AccessUtil.canUseExternalLink;

public abstract class YouTrackPage implements Activity {

    @PostConstruct
    public void onInit() {
        TAB = "YouTrack";
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        if (canUseExternalLink(event.profile)) {
            fireEvent(new MenuEvents.Add(TAB, UiConstants.TabIcons.YOUTRACK, TAB, YOUTRACK_URL, DebugIds.SIDEBAR_MENU.YOUTRACK));
        }
    }

    private String TAB;
    private final static String YOUTRACK_URL = "https://youtrack.protei.ru/issues";
}
