package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Set;

public class PersonSubscriptionChangeRequest extends AuditableObject {

    public static final String AUDIT_TYPE = "PersonSubscriptionChangeRequest";

    private Long notifierId;
    private Set<PersonShortView> persons;

    public PersonSubscriptionChangeRequest() {}

    public PersonSubscriptionChangeRequest(Long notifierId, Set<PersonShortView> persons) {
        this.notifierId = notifierId;
        this.persons = persons;
    }

    @Override
    public String getAuditType() {
        return AUDIT_TYPE;
    }

    @Override
    public Long getId() {
        return notifierId;
    }

    public Set<PersonShortView> getPersons() {
        return persons;
    }
}
