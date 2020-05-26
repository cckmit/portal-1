package ru.protei.portal.ui.ipreservation.client.page;

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
            fireEvent( new MenuEvents.Add( ТAB, UiConstants.TabIcons.IP_RESERVATION, ТAB, DebugIds.SIDEBAR_MENU.RESERVED_IP ) );
            fireEvent( new AppEvents.InitPage(showReservedIp) );
        }
    }

    @Event
    public void onShowTable( IpReservationEvents.ShowReservedIp event ) {
        fireSelectTab();
    }

    @Event
    public void onShowSubnet( IpReservationEvents.ShowSubnet event ) {
        fireSelectTab();
    }

    @Event
    public void onClickSection( SectionEvents.Clicked event ) {
        if ( !ТAB.equals( event.identity ) ) {
            return;
        }

        fireSelectTab();
        fireEvent(showReservedIp);
    }

    private void fireSelectTab() {
        fireEvent( new ActionBarEvents.Clear() );
        if ( policyService.hasAnyPrivilegeOf( En_Privilege.SUBNET_VIEW, En_Privilege.RESERVED_IP_VIEW ) ) {
            fireEvent(new MenuEvents.Select(ТAB));
        }
    }

    @Inject
    Lang lang;
    @Inject
    private PolicyService policyService;

    private String ТAB;
    private IpReservationEvents.ShowReservedIp showReservedIp = new IpReservationEvents.ShowReservedIp();
}