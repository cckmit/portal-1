package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.core.model.ent.Person;

public class EducationRequestEvent extends ApplicationEvent {
    private Person initiator;
    private EducationEntry educationEntry;
    private Person headOfDepartment;

    public EducationRequestEvent(Object source, Person initiator, Person headOfDepartment, EducationEntry educationEntry) {
        super(source);
        this.initiator = initiator;
        this.educationEntry = educationEntry;
        this.headOfDepartment = headOfDepartment;
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

    public Person getHeadOfDepartment() {
        return headOfDepartment;
    }
}
