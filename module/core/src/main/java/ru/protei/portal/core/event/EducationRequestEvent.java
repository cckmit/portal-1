package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Person;

public class EducationRequestEvent extends ApplicationEvent implements AbstractEducationEvent {
    private Person person;
    private Long educationId;

    public EducationRequestEvent(Object source, Person person, Long educationId) {
        super(source);
        this.person = person;
        this.educationId = educationId;
    }

    @Override
    public Person getPerson() {
        return person;
    }

    @Override
    public Long getEducationId() {
        return educationId;
    }

    @Override
    public boolean isCreateEvent() {
        return true;
    }
}
