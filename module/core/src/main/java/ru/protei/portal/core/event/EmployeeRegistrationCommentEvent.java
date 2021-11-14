package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.CaseComment;

public class EmployeeRegistrationCommentEvent extends ApplicationEvent implements AbstractEmployeeRegistrationEvent {
    private CaseComment oldComment;
    private CaseComment newComment;
    private CaseComment removedComment;
    private Long personId;
    private Long employeeRegistrationId;

    public EmployeeRegistrationCommentEvent(Object source, CaseComment oldComment, CaseComment newComment, CaseComment removedComment, Long personId, Long employeeRegistrationId) {
        super(source);
        this.source = source;
        this.oldComment = oldComment;
        this.newComment = newComment;
        this.removedComment = removedComment;
        this.personId = personId;
        this.employeeRegistrationId = employeeRegistrationId;
    }

    @Override
    public Long getPersonId() {
        return personId;
    }

    @Override
    public Long getEmployeeRegistrationId() {
        return employeeRegistrationId;
    }

    public CaseComment getOldComment() {
        return oldComment;
    }

    public CaseComment getNewComment() {
        return newComment;
    }

    public CaseComment getRemovedComment() {
        return removedComment;
    }
}
