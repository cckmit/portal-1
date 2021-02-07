package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.core.model.ent.Person;

import java.util.List;
import java.util.Set;

public class EducationRequestApproveEvent extends ApplicationEvent {
    private List<Person> approvedParticipants;
    private EducationEntry educationEntry;
    private Set<Person> headsOfDepartments;

    public EducationRequestApproveEvent(Object source, List<Person> approvedParticipants, Set<Person> headsOfDepartments,
                                        EducationEntry educationEntry) {
        super(source);
        this.approvedParticipants = approvedParticipants;
        this.educationEntry = educationEntry;
        this.headsOfDepartments = headsOfDepartments;
    }

    public List<Person> getApprovedParticipants() {
        return approvedParticipants;
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
