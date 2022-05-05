package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Person;

import java.util.Set;

public class CaseObjectDeadlineExpireEvent extends ApplicationEvent {
    private final Set<Person> notifiers;
    private final Long caseObjectId;
    private final Long caseNumber;

    private final boolean isPrivateCase;

    public CaseObjectDeadlineExpireEvent(Object source, Set<Person> notifiers, Long caseObjectId, Long caseNumber, boolean isPrivateCase) {
        super(source);
        this.notifiers = notifiers;
        this.caseObjectId = caseObjectId;
        this.caseNumber = caseNumber;
        this.isPrivateCase = isPrivateCase;
    }

    public Set<Person> getNotifiers() {
        return notifiers;
    }

    public Long getCaseObjectId() {
        return caseObjectId;
    }

    public Long getCaseNumber() {
        return caseNumber;
    }

    public boolean isPrivateCase() {
        return isPrivateCase;
    }

    @Override
    public String toString() {
        return "CaseObjectDeadlineExpireEvent{" +
                "notifiers=" + notifiers +
                ", caseObjectId=" + caseObjectId +
                ", caseNumber=" + caseNumber +
                ", isPrivateCase=" + isPrivateCase +
                '}';
    }
}
