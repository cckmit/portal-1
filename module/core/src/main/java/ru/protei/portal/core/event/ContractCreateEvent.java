package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.struct.NotificationEntry;

import java.util.Set;

public class ContractCreateEvent extends ApplicationEvent {

    public ContractCreateEvent(Object source, Contract contract, Set<NotificationEntry> notificationEntryList) {
        super(source);
        this.contract = contract;
        this.notificationEntryList = notificationEntryList;
    }

    public Contract getContract() {
        return contract;
    }

    public Set<NotificationEntry> getNotificationEntryList() {
        return notificationEntryList;
    }

    private final Contract contract;
    private final Set<NotificationEntry> notificationEntryList;
}
