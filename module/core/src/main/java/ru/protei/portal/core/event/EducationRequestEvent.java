package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.core.model.ent.Person;

public class EducationRequestEvent extends ApplicationEvent implements AbstractEducationEvent {
    private Person person;
    private EducationEntry educationEntry;

    public EducationRequestEvent(Object source, Person person, EducationEntry educationEntry) {
        super(source);
        this.person = person;
        this.educationEntry = educationEntry;
    }

    @Override
    public Person getPerson() {
        return person;
    }

    @Override
    public EducationEntry getEducationEntry() {
        return educationEntry;
    }

    @Override
    public boolean isCreateEvent() {
        return true;
    }
}
