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

public abstract class DeliveryPage
        implements Activity {

    @PostConstruct
    public void onInit() {
        ТAB = lang.deliveries();
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        if ( event.profile.hasPrivilegeFor( En_Privilege.DELIVERY_VIEW) ) {
            fireEvent( new MenuEvents.Add( ТAB, UiConstants.TabIcons.DELIVERY, ТAB, DebugIds.SIDEBAR_MENU.DELIVERY) );
            fireEvent( new AppEvents.InitPage( new DeliveryEvents.Show( false ) ) );
        }
    }

    @Event
    public void onShowTable( DeliveryEvents.Show event ) {
        fireSelectTab();
    }

    @Event
    public void onShowDetail( DeliveryEvents.Edit event ) {
        fireSelectTab();
    }

    @Event
    public void onShowPreview(DeliveryEvents.ShowFullScreen event) {
        fireSelectTab();
    }

    @Event
    public void onClickSection( SectionEvents.Clicked event ) {
        if ( !ТAB.equals( event.identity ) ) {
            return;
        }

        fireSelectTab();
        fireEvent( new DeliveryEvents.Show( false ) );
    }

    private void fireSelectTab() {
        fireEvent( new ActionBarEvents.Clear() );
        if ( policyService.hasPrivilegeFor( En_Privilege.DELIVERY_VIEW) ) {
            fireEvent( new MenuEvents.Select( ТAB ) );
        }
    }


    @Inject
    Lang lang;

    @Inject
    PolicyService policyService;

    private String ТAB;
}

