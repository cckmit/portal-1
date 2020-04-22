package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;

public class RoomReservationEvents {

    @Url(value = "room_reservation", primary = true)
    public static class Show {
        public Show() {}
    }
}
