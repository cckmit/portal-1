package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.core.model.struct.NotificationEntry;

import java.util.List;

public class SubnetNotificationEvent extends ApplicationEvent {

    private final Subnet subnet;
    private final Person initiator;
    private final Action action;
    private final List<NotificationEntry> notificationEntryList;

    public SubnetNotificationEvent(Object source, Subnet subnet, Person initiator, Action action, List<NotificationEntry> notificationEntryList) {
        super(source);
        this.subnet = subnet;
        this.initiator = initiator;
        this.action = action;
        this.notificationEntryList = notificationEntryList;
    }

    public Subnet getSubnet() {
        return subnet;
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