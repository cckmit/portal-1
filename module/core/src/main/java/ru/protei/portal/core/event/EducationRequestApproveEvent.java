package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.core.model.ent.Person;

import java.util.List;

public class EducationRequestApproveEvent extends ApplicationEvent {
    private Person initiator;
    private EducationEntry educationEntry;
    private List<Long> workersApproved;
    private Person headOfDepartment;

    public EducationRequestApproveEvent(Object source, Person initiator, Person headOfDepartment,
                                        EducationEntry educationEntry, List<Long> workersApproved) {
        super(source);
        this.initiator = initiator;
        this.educationEntry = educationEntry;
        this.workersApproved = workersApproved;
        this.headOfDepartment = headOfDepartment;
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

    public Person getHeadOfDepartment() {
        return headOfDepartment;
    }
}
