package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.CaseComment;

public class DeliveryCommentEvent extends ApplicationEvent implements AbstractDeliveryEvent {
    private CaseComment oldComment;
    private CaseComment newComment;
    private CaseComment removedComment;

    private Long personId;
    private Long deliveryId;

    public DeliveryCommentEvent(Object source, CaseComment oldComment, CaseComment newComment, CaseComment removedComment, Long personId, Long deliveryId) {
        super(source);
        this.oldComment = oldComment;
        this.newComment = newComment;
        this.removedComment = removedComment;
        this.personId = personId;
        this.deliveryId = deliveryId;
        this.source = source;
    }

    @Override
    public Long getPersonId() {
        return personId;
    }

    @Override
    public Long getDeliveryId() {
        return deliveryId;
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
