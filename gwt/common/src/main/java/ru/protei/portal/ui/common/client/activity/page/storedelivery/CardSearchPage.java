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

public abstract class CardSearchPage implements Activity {

    @PostConstruct
    public void onInit() {
        CATEGORY = lang.storeAndDelivery();
        TAB = "Поиск платы";
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        fireEvent(new MenuEvents.Add(TAB, UiConstants.TabIcons.CARD_SEARCH, TAB, CARD_SEARCH_URL, DebugIds.SIDEBAR_MENU.CARD_SEARCH).withParent(CATEGORY));
    }

    @Inject
    Lang lang;

    private String CATEGORY;
    private String TAB;
    private final static String CARD_SEARCH_URL = "http://portal/sd/store/card_info.jsp";
}
