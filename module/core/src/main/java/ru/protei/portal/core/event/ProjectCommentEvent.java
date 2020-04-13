package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.CaseComment;

public class ProjectCommentEvent extends ApplicationEvent implements AbstractProjectEvent {
    private CaseComment oldComment;
    private CaseComment newComment;
    private CaseComment removedComment;

    private Long personId;
    private Long projectId;
    private Object source;

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

    @Override
    public Object getSource() {
        return source;
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
