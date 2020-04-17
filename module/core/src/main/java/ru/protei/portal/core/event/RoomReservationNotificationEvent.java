package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.RoomReservation;
import ru.protei.portal.core.model.struct.NotificationEntry;

import java.util.List;

public class RoomReservationNotificationEvent extends ApplicationEvent {

    private final RoomReservation roomReservation;
    private final Action action;
    private final List<NotificationEntry> notificationEntryList;

    public RoomReservationNotificationEvent(Object source, RoomReservation roomReservation, Action action, List<NotificationEntry> notificationEntryList) {
        super(source);
        this.roomReservation = roomReservation;
        this.action = action;
        this.notificationEntryList = notificationEntryList;
    }

    public RoomReservation getRoomReservation() {
        return roomReservation;
    }

    public Action getAction() {
        return action;
    }

    public List<NotificationEntry> getNotificationEntryList() {
        return notificationEntryList;
    }

    public enum Action {
        CREATED,
        UPDATED,
        REMOVED,
        ;
    }
}
