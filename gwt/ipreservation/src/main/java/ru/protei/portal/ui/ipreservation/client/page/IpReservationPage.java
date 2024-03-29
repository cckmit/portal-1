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
        CATEGORY = lang.ipReservation();
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        if (policyService.hasAnyPrivilegeOf(En_Privilege.RESERVED_IP_VIEW, En_Privilege.SUBNET_VIEW)) {
            fireEvent(new MenuEvents.Add(CATEGORY, UiConstants.TabIcons.IP_RESERVATION, CATEGORY, DebugIds.SIDEBAR_MENU.IP_RESERVATION));
        }
    }

    @Event
    public void onClickSection( SectionEvents.Clicked event ) {
        if ( !CATEGORY.equals( event.identity ) ) {
            return;
        }

        fireEvent(new IpReservationEvents.ShowReservedIp());
    }

    @Inject
    Lang lang;
    @Inject
    private PolicyService policyService;

    private String CATEGORY;
}