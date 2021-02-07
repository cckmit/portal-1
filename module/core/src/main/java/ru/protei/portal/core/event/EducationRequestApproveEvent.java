package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.core.model.ent.Person;

import java.util.List;
import java.util.Set;

public class EducationRequestApproveEvent extends ApplicationEvent {
    private Person initiator;
    private EducationEntry educationEntry;
    private List<Long> workersApproved;
    private Set<Person> headsOfDepartments;

    public EducationRequestApproveEvent(Object source, Person initiator, Set<Person> headsOfDepartments,
                                        EducationEntry educationEntry, List<Long> workersApproved) {
        super(source);
        this.initiator = initiator;
        this.educationEntry = educationEntry;
        this.workersApproved = workersApproved;
        this.headsOfDepartments = headsOfDepartments;
    }

    public Person getInitiator() {
        return initiator;
    }

    public EducationEntry getEducationEntry() {
        return educationEntry;
    }

    public List<Long> getWorkersApproved() {
        return workersApproved;
    }

    public boolean isCreateEvent() {
        return true;
    }

    public Set<Person> getHeadsOfDepartments() {
        return headsOfDepartments;
    }
}
