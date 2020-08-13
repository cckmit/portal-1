package ru.protei.portal.ui.roomreservation.client.page;

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
    public void onShow(RoomReservationEvents.Show event) {
        fireEvent(new ActionBarEvents.Clear());
        fireEvent(new ActionBarEvents.Add(lang.table(), "", UiConstants.ActionBarIdentity.ROOM_RESERVATION_TABLE));
    }

    @Event
    public void onShow(RoomReservationEvents.ShowTable event) {
        fireEvent(new ActionBarEvents.Clear());
        fireEvent(new ActionBarEvents.Add(lang.calendar(), "", UiConstants.ActionBarIdentity.ROOM_RESERVATION_CALENDAR));
    }

    @Event
    public void onShowCalendarClicked(ActionBarEvents.Clicked event) {
        if (!(UiConstants.ActionBarIdentity.ROOM_RESERVATION_CALENDAR.equals(event.identity)) ) {
            return;
        }

        fireEvent(show);
    }

    @Event
    public void onShowTableClicked(ActionBarEvents.Clicked event) {
        if (!(UiConstants.ActionBarIdentity.ROOM_RESERVATION_TABLE.equals(event.identity)) ) {
            return;
        }

        fireEvent(showTable);
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
    private RoomReservationEvents.ShowTable showTable = new RoomReservationEvents.ShowTable();
}
