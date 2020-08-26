package ru.protei.portal.ui.common.client.activity.page.archive;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;

public abstract class FeatureRequestPage implements Activity {

    @PostConstruct
    public void onInit() {
        CATEGORY = lang.archive();
        TAB = "Feature Request";
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        fireEvent(new MenuEvents.Add(TAB, UiConstants.TabIcons.FEATURE_REQUEST, TAB, FEATURE_REQUEST_URL, DebugIds.SIDEBAR_MENU.FEATURE_REQUEST).withParent(CATEGORY));
    }

    @Inject
    Lang lang;

    private String CATEGORY;
    private String TAB;
    private final static String FEATURE_REQUEST_URL = "https://oldportal.protei.ru/frq/index.jsp";
}
