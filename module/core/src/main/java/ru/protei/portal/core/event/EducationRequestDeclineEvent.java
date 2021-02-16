package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.core.model.ent.Person;

import java.util.List;
import java.util.Set;

public class EducationRequestDeclineEvent extends ApplicationEvent {
    private List<Person> declineParticipants;
    private EducationEntry educationEntry;
    private Set<Person> headsOfDepartments;

    public EducationRequestDeclineEvent(Object source, List<Person> declineParticipants, Set<Person> headsOfDepartments,
                                        EducationEntry educationEntry) {
        super(source);
        this.declineParticipants = declineParticipants;
        this.educationEntry = educationEntry;
        this.headsOfDepartments = headsOfDepartments;
    }

    public List<Person> getDeclineParticipants() {
        return declineParticipants;
    }

    public EducationEntry getEducationEntry() {
        return educationEntry;
    }

    public Set<Person> getHeadsOfDepartments() {
        return headsOfDepartments;
    }
}
