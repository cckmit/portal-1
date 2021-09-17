package ru.protei.portal.ui.delivery.client.activity.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

public abstract class StoreAndDeliveryPage
        implements Activity {

    @PostConstruct
    public void onInit() {
        CATEGORY = lang.newStoreAndDelivery();
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        if ( policyService.hasAnyPrivilegeOf( En_Privilege.DELIVERY_VIEW, En_Privilege.DELIVERY_EDIT, En_Privilege.DELIVERY_CREATE ) ) {
            fireEvent( new MenuEvents.Add( CATEGORY, UiConstants.TabIcons.STORE_AND_DELIVERY, CATEGORY, DebugIds.SIDEBAR_MENU.STORE_AND_DELIVERY ) );
        }
    }

    @Event
    public void onClickSection( SectionEvents.Clicked event ) {
        if ( !CATEGORY.equals( event.identity ) ) {
            return;
        }

        fireEvent(new DeliveryEvents.Show(false));
    }

    @Inject
    Lang lang;
    @Inject
    private PolicyService policyService;

    private String CATEGORY;
}

