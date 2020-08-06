package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.CaseComment;

public class ProjectCommentEvent extends ApplicationEvent implements AbstractProjectEvent {
    private CaseComment oldComment;
    private CaseComment newComment;
    private CaseComment removedComment;

    private Long personId;
    private Long projectId;

    public ProjectCommentEvent(Object source, CaseComment oldComment, CaseComment newComment, CaseComment removedComment, Long personId, Long projectId) {
        super(source);
        this.oldComment = oldComment;
        this.newComment = newComment;
        this.removedComment = removedComment;
        this.personId = personId;
        this.projectId = projectId;
        this.source = source;
    }

    @Override
    public Long getPersonId() {
        return personId;
    }

    @Override
    public Long getProjectId() {
        return projectId;
    }

    CaseComment getOldComment() {
        return oldComment;
    }

    CaseComment getNewComment() {
        return newComment;
    }

    CaseComment getRemovedComment() {
        return removedComment;
    }
}
