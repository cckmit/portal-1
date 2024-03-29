package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.struct.NotificationEntry;

import java.util.Arrays;
import java.util.List;

public class ReservedIpNotificationEvent extends ApplicationEvent {

    private final List<ReservedIp> reservedIps;
    private final Person initiator;
    private final Action action;
    private final List<NotificationEntry> notificationEntryList;

    public ReservedIpNotificationEvent(Object source, ReservedIp reservedIp,
                                       Person initiator, Action action,
                                       List<NotificationEntry> notificationEntryList) {
        super(source);
        this.reservedIps = Arrays.asList(reservedIp);
        this.initiator = initiator;
        this.action = action;
        this.notificationEntryList = notificationEntryList;
    }


    public ReservedIpNotificationEvent(Object source, List<ReservedIp> reservedIps,
                                       Person initiator, Action action,
                                       List<NotificationEntry> notificationEntryList) {
        super(source);
        this.reservedIps = reservedIps;
        this.initiator = initiator;
        this.action = action;
        this.notificationEntryList = notificationEntryList;
    }

    public List<ReservedIp> getReservedIps() {
        return reservedIps;
    }

    public Person getInitiator() { return initiator; }

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