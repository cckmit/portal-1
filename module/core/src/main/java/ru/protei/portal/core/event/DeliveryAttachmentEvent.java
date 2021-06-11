package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Attachment;

import java.util.Collection;

public class DeliveryAttachmentEvent extends ApplicationEvent implements AbstractDeliveryEvent {
    private Collection<Attachment> addedAttachments;
    private Collection<Attachment> removedAttachments;
    private Long commentId;

    private Long personId;
    private Long deliveryId;

    public DeliveryAttachmentEvent(Object source, Collection<Attachment> addedAttachments, Collection<Attachment> removedAttachments, Long commentId, Long personId, Long deliveryId) {
        super(source);
        this.addedAttachments = addedAttachments;
        this.removedAttachments = removedAttachments;
        this.personId = personId;
        this.deliveryId = deliveryId;
        this.commentId = commentId;
    }

    @Override
    public Long getPersonId() {
        return personId;
    }

    @Override
    public Long getDeliveryId() {
        return deliveryId;
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
