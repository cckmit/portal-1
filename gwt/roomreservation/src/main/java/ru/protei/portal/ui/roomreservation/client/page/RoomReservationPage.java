package ru.protei.portal.ui.roomreservation.client.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

import java.util.Date;

import static ru.protei.portal.ui.common.client.util.DateUtils.resetTime;

public abstract class RoomReservationPage implements Activity {

    @PostConstruct
    public void onInit() {
        CATEGORY = lang.roomReservation();
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        if (event.profile.hasPrivilegeFor(En_Privilege.ROOM_RESERVATION_VIEW)) {
            fireEvent(new MenuEvents.Add(CATEGORY, UiConstants.TabIcons.ROOM_RESERVATION, CATEGORY, DebugIds.SIDEBAR_MENU.ROOM_RESERVATION));
        }
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!(UiConstants.ActionBarIdentity.ROOM_RESERVATION_CREATE.equals(event.identity)) ) {
            return;
        }

        Date date = new Date();
        Date d = resetTime(date);
        d.setHours(date.getHours() + 1);
        fireEvent(new RoomReservationEvents.Create(null, d));
    }


    @Event
    public void onClickSection( SectionEvents.Clicked event ) {
        if ( !CATEGORY.equals( event.identity ) ) {
            return;
        }

        fireEvent(new RoomReservationEvents.Show());
    }

    @Inject
    Lang lang;

    private String CATEGORY;
}
