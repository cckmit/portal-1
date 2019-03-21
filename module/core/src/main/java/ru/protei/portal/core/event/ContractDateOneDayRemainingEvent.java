package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.core.model.struct.NotificationEntry;

import java.util.Set;

public class ContractDateOneDayRemainingEvent extends ApplicationEvent {

    private Contract contract;
    private ContractDate contractDate;
    private Set<NotificationEntry> notificationEntryList;


    public ContractDateOneDayRemainingEvent(Object source) {
        super(source);
    }

    public ContractDateOneDayRemainingEvent(Object source, Contract contract, ContractDate contractDate, Set<NotificationEntry> notificationEntryList) {
        super(source);
        this.contract = contract;
        this.contractDate = contractDate;
        this.notificationEntryList = notificationEntryList;
    }

    public Contract getContract() {
        return contract;
    }

    public ContractDate getContractDate() {
        return contractDate;
    }

    public Set<NotificationEntry> getNotificationEntrySet() {
        return notificationEntryList;
    }
}
