package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.RoomReservable;

import java.util.Date;

public class RoomReservationEvents {

    @Url(value = "room_reservation", primary = true)
    public static class Show {
        public Show() {}
    }

    public static class Reload {
        public Reload() {}
    }

    public static class Create {
        public RoomReservable room;
        public Date date;
        public Create() {}
        public Create(RoomReservable room, Date date) {
            this.room = room;
            this.date = date;
        }
    }

    public static class Edit {
        public Long roomReservationId;
        public Edit() {}
        public Edit(Long roomReservationId) {
            this.roomReservationId = roomReservationId;
        }
    }
}
