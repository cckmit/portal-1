package ru.protei.portal.ui.ipreservation.client.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.IpReservationEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

/**
 * Активность по работе с вкладкой "Резервирование IP"
 */
public abstract class IpReservationPage
        implements Activity {

    @PostConstruct
    public void onInit() {
        ТAB = lang.ipReservation();
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        if ( event.profile.hasPrivilegeFor( En_Privilege.RESERVED_IP_VIEW ) ) {
            fireEvent( new MenuEvents.Add( ТAB, UiConstants.TabIcons.RESERVED_IP, ТAB, DebugIds.SIDEBAR_MENU.RESERVED_IP ) );
            fireEvent( new AppEvents.InitPage( new IpReservationEvents.Show( ) ) );
        }
    }

    @Event
    public void onShowTable( IpReservationEvents.Show event ) {
        fireSelectTab();
    }

    @Event
    public void onShowSubnetDetail( IpReservationEvents.EditSubnet event ) {
        fireSelectTab();
    }

    @Event
    public void onShowReservedIpDetail( IpReservationEvents.EditReservedIp event ) {
        fireSelectTab();
    }

    @Event
    public void onClickSection( SectionEvents.Clicked event ) {
        if ( !ТAB.equals( event.identity ) ) {
            return;
        }

        fireSelectTab();
        fireEvent( new IpReservationEvents.Show( ) );
    }

    private void fireSelectTab() {
        fireEvent( new ActionBarEvents.Clear() );
        if ( policyService.hasPrivilegeFor( En_Privilege.RESERVED_IP_VIEW ) ) {
            fireEvent(new MenuEvents.Select(ТAB));
        }
    }


    @Inject
    Lang lang;
    @Inject
    private PolicyService policyService;

    private String ТAB;
}

