package ru.protei.portal.ui.roomreservation.client.page;

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
import ru.protei.portal.ui.common.client.events.RoomReservationEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

public abstract class CalendarPage implements Activity {

    @PostConstruct
    public void onInit() {
        CATEGORY = lang.roomReservation();
        TAB = lang.calendar();
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        if (event.profile.hasPrivilegeFor(En_Privilege.ROOM_RESERVATION_VIEW)) {
            fireEvent(new MenuEvents.Add(TAB, UiConstants.TabIcons.ROOM_RESERVATION_CALENDAR, TAB,
                                         CrmConstants.PAGE_LINK.ROOM_RESERVATION_CALENDAR,
                                         DebugIds.SIDEBAR_MENU.ROOM_RESERVATION_CALENDAR).withParent(CATEGORY));
            fireEvent(new AppEvents.InitPage(show));
        }
    }

    @Event
    public void onShow(RoomReservationEvents.Show event) {
        fireSelectTab();
    }

    @Event
    public void onClickSection(SectionEvents.Clicked event) {
        if (!TAB.equals(event.identity)) {
            return;
        }
        fireSelectTab();
        fireEvent(show);
    }

    private void fireSelectTab() {
        fireEvent(new ActionBarEvents.Clear());
        if (policyService.hasPrivilegeFor(En_Privilege.ROOM_RESERVATION_VIEW)) {
            fireEvent(new MenuEvents.Select(TAB, CATEGORY));
        }
    }

    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;

    private String CATEGORY;
    private String TAB;
    private RoomReservationEvents.Show show = new RoomReservationEvents.Show();
}
