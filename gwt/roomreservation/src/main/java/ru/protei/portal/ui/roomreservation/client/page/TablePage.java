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

public abstract class TablePage implements Activity {

    @PostConstruct
    public void onInit() {
        CATEGORY = lang.roomReservation();
        TAB = lang.generalTable();
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        if (event.profile.hasPrivilegeFor(En_Privilege.ROOM_RESERVATION_VIEW)) {
            fireEvent(new MenuEvents.Add(TAB, UiConstants.TabIcons.ROOM_RESERVATION_TABLE, TAB,
                                         CrmConstants.PAGE_LINK.ROOM_RESERVATION_TABLE,
                                         DebugIds.SIDEBAR_MENU.ROOM_RESERVATION_TABLE).withParent(CATEGORY));
            fireEvent(new AppEvents.InitPage(show));
        }
    }

    @Event
    public void onShow(RoomReservationEvents.ShowTable event) {
        fireSelectTab();
        fireEvent(new ActionBarEvents.Add(lang.buttonCreate(), "", UiConstants.ActionBarIdentity.ROOM_RESERVATION_CREATE));
    }

    @Event
    public void onShowTableClicked(ActionBarEvents.Clicked event) {
        if (!(UiConstants.ActionBarIdentity.ROOM_RESERVATION_TABLE.equals(event.identity))) {
            return;
        }

        fireEvent(show);
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
    private RoomReservationEvents.ShowTable show = new RoomReservationEvents.ShowTable();
}
