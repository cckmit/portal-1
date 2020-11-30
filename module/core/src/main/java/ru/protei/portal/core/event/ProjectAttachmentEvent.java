package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Attachment;

import java.util.Collection;

public class ProjectAttachmentEvent extends ApplicationEvent implements AbstractProjectEvent {
    private Collection<Attachment> addedAttachments;
    private Collection<Attachment> removedAttachments;
    private Long commentId;

    private Long personId;
    private Long projectId;

    public ProjectAttachmentEvent(Object source, Collection<Attachment> addedAttachments, Collection<Attachment> removedAttachments, Long commentId, Long personId, Long projectId) {
        super(source);
        this.addedAttachments = addedAttachments;
        this.removedAttachments = removedAttachments;
        this.personId = personId;
        this.projectId = projectId;
        this.commentId = commentId;
    }

    @Override
    public Long getPersonId() {
        return personId;
    }

    @Override
    public Long getProjectId() {
        return projectId;
    }

    public Collection<Attachment> getAddedAttachments() {
        return addedAttachments;
    }

    public Collection<Attachment> getRemovedAttachments() {
        return removedAttachments;
    }

    public Long getCommentId() {
        return commentId;
    }
}
