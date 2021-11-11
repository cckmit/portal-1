package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.CaseComment;

public class EmployeeRegistrationCommentEvent extends ApplicationEvent {
    public EmployeeRegistrationCommentEvent(Object source, CaseComment comment, Long personId, Long employeeRegistrationId ) {
        super( source );
        this.comment = comment;
        this.personId = personId;
        this.employeeRegistrationId = employeeRegistrationId;
    }


    private CaseComment comment;
    private Long personId;
    private Long employeeRegistrationId;

    @Override
    public String toString() {
        return "EmployeeRegistrationCommentEvent{" +
                "comment='" + comment + '\'' +
                "personId='" + personId +
                "employeeRegistrationId='" + employeeRegistrationId +
                '}';
    }
}
