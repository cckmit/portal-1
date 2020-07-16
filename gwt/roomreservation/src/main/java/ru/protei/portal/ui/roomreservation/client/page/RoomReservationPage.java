package ru.protei.portal.ui.roomreservation.client.page;

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
import ru.protei.portal.ui.common.client.events.RoomReservationEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

public abstract class RoomReservationPage implements Activity {

    @PostConstruct
    public void onInit() {
        TAB = lang.roomReservation();
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        if (event.profile.hasPrivilegeFor(En_Privilege.ROOM_RESERVATION_VIEW)) {
            fireEvent(new MenuEvents.Add(TAB, UiConstants.TabIcons.ROOM_RESERVATION, TAB, DebugIds.SIDEBAR_MENU.ROOM_RESERVATION));
            fireEvent(new AppEvents.InitPage(show));
        }
    }

    @Event
    public void onClickSection(SectionEvents.Clicked event) {
        if (!TAB.equals(event.identity)) {
            return;
        }
        fireSelectTab();
        fireEvent(show);
    }

    @Event
    public void onShowTable(RoomReservationEvents.Show event) {
        fireSelectTab();
    }

    private void fireSelectTab() {
        fireEvent(new ActionBarEvents.Clear());
        if (policyService.hasPrivilegeFor(En_Privilege.ROOM_RESERVATION_VIEW)) {
            fireEvent(new MenuEvents.Select(TAB));
        }
    }

    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;

    private String TAB;
    private RoomReservationEvents.Show show = new RoomReservationEvents.Show();
}
