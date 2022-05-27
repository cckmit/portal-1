package ru.protei.portal.ui.ipreservation.client.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.util.CrmConstants;
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

public abstract class SubnetPage implements Activity {

    @PostConstruct
    public void onInit() {
        CATEGORY = lang.ipReservation();
        ТAB = lang.subnets();
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        if (event.profile.hasPrivilegeFor(En_Privilege.SUBNET_VIEW)) {
                fireEvent(new MenuEvents.Add(ТAB, UiConstants.TabIcons.SUBNET, ТAB,
                                             CrmConstants.PAGE_LINK.SUBNET,
                                             DebugIds.SIDEBAR_MENU.SUBNET).withParent(CATEGORY));
            fireEvent(new AppEvents.InitPage(showSubnet));
        }
    }

    @Event
    public void onShowTable(IpReservationEvents.ShowSubnet event) {
        fireSelectTab();
    }

    @Event
    public void onClickSection(SectionEvents.Clicked event) {
        if (!ТAB.equals(event.identity)) {
            return;
        }

        fireSelectTab();
        fireEvent(showSubnet);
    }

    private void fireSelectTab() {
        fireEvent(new ActionBarEvents.Clear());
        if (policyService.hasPrivilegeFor(En_Privilege.SUBNET_VIEW)) {
            fireEvent(new MenuEvents.Select(ТAB, CATEGORY));
        }
    }

    @Inject
    Lang lang;
    @Inject
    private PolicyService policyService;

    private String CATEGORY;
    private String ТAB;
    private IpReservationEvents.ShowSubnet showSubnet = new IpReservationEvents.ShowSubnet();
}
