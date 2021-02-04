package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.core.model.ent.Person;

public class EducationRequestEvent extends ApplicationEvent implements AbstractEducationEvent {
    private Person initiator;
    private EducationEntry educationEntry;
    private String typeName;

    public EducationRequestEvent(Object source, Person initiator, EducationEntry educationEntry, String typeName) {
        super(source);
        this.initiator = initiator;
        this.educationEntry = educationEntry;
        this.typeName = typeName;
    }

    @Override
    public Person getInitiator() {
        return initiator;
    }

    @Override
    public EducationEntry getEducationEntry() {
        return educationEntry;
    }

    @Override
    public boolean isCreateEvent() {
        return true;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }
}
