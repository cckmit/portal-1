package ru.protei.portal.ui.common.client.activity.page.storedelivery;

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

public abstract class StorePage implements Activity {

    @PostConstruct
    public void onInit() {
        CATEGORY = lang.storeAndDelivery();
        TAB = lang.store();
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        if (canUseExternalLink(event.profile)) {
            fireEvent(new MenuEvents.Add(TAB, UiConstants.TabIcons.STORE, TAB, STORE_URL, DebugIds.SIDEBAR_MENU.STORE).withParent(CATEGORY));
        }
    }

    @Inject
    Lang lang;

    private String CATEGORY;
    private String TAB;
    private final static String STORE_URL = "https://oldportal.protei.ru/sd/index.jsp";
}
