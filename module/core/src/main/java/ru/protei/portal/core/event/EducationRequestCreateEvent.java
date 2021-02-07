package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.core.model.ent.Person;

import java.util.List;
import java.util.Set;

public class EducationRequestCreateEvent extends ApplicationEvent {
    private List<Person> participants;
    private EducationEntry educationEntry;
    private Set<Person> headsOfDepartments;

    public EducationRequestCreateEvent(Object source, List<Person> participants, Set<Person> headsOfDepartments,
                                       EducationEntry educationEntry) {
        super(source);
        this.participants = participants;
        this.educationEntry = educationEntry;
        this.headsOfDepartments = headsOfDepartments;
    }

    public List<Person> getParticipants() {
        return participants;
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
