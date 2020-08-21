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

public abstract class DeliveryPage implements Activity {

    @PostConstruct
    public void onInit() {
        CATEGORY = lang.storeAndDelivery();
        TAB = lang.delivery();
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        fireEvent(new MenuEvents.Add(TAB, UiConstants.TabIcons.DELIVERY, TAB, DELIVERY_URL, DebugIds.SIDEBAR_MENU.DELIVERY).withParent(CATEGORY));
    }

    @Inject
    Lang lang;

    private String CATEGORY;
    private String TAB;
    private final static String DELIVERY_URL = "http://portal/sd/delivery/delivery.jsp";
}
