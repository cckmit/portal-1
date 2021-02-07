package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.core.model.ent.Person;

import java.util.Set;

public class EducationRequestEvent extends ApplicationEvent {
    private Person initiator;
    private EducationEntry educationEntry;
    private Set<Person> headsOfDepartments;

    public EducationRequestEvent(Object source, Person initiator, Set<Person> headsOfDepartments, EducationEntry educationEntry) {
        super(source);
        this.initiator = initiator;
        this.educationEntry = educationEntry;
        this.headsOfDepartments = headsOfDepartments;
    }

    public Person getInitiator() {
        return initiator;
    }

    public EducationEntry getEducationEntry() {
        return educationEntry;
    }

    public boolean isCreateEvent() {
        return true;
    }

    public Set<Person> getHeadsOfDepartments() {
        return headsOfDepartments;
    }
}
