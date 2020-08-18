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

public abstract class YouTrackPage implements Activity {

    @PostConstruct
    public void onInit() {
        TAB = "YouTrack";
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        fireEvent(new MenuEvents.Add(TAB, UiConstants.TabIcons.YOUTRACK, TAB, YOUTRACK_URL, DebugIds.SIDEBAR_MENU.YOUTRACK));
    }

    @Inject
    Lang lang;

    private String TAB;
    private final static String YOUTRACK_URL = "https://youtrack.protei.ru/issues";
}
