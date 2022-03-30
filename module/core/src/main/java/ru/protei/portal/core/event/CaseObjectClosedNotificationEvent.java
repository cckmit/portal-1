package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Person;

public class CaseObjectClosedNotificationEvent extends ApplicationEvent {
    private final Person customer;
    private final Long caseObjectId;
    private final Long caseNumber;

    public CaseObjectClosedNotificationEvent(Object source, Person customer, Long caseObjectId, Long caseNumber) {
        super(source);
        this.customer = customer;
        this.caseObjectId = caseObjectId;
        this.caseNumber = caseNumber;
    }

    public Person getCustomer() {
        return customer;
    }

    public Long getCaseObjectId() {
        return caseObjectId;
    }

    public Long getCaseNumber() {
        return caseNumber;
    }

    @Override
    public String toString() {
        return "CaseObjectClosedNotificationEvent{" +
                "customer=" + customer +
                ", caseObjectId=" + caseObjectId +
                ", caseNumber=" + caseNumber +
                '}';
    }
}
