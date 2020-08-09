package ru.protei.portal.core.model.event;

/**
 *
 */
public class CaseCommentRemovedClientEvent implements de.novanic.eventservice.client.event.Event {

    private Long caseObjectId;
    private Long commentId;
    private Long personId;

    public CaseCommentRemovedClientEvent() {
    }

    public CaseCommentRemovedClientEvent( Long personId, Long caseObjectId, Long commentId ) {
        this.personId = personId;
        this.caseObjectId = caseObjectId;
        this.commentId = commentId;
    }

    public Long getCaseObjectId() {
        return caseObjectId;
    }

    public Long getCaseCommentID() {
        return commentId;
    }

    public Long getPersonId() {
        return personId;
    }

    @Override
    public String toString() {
        return "CaseCommentRemovedClientEvent{" +
                "caseObjectId=" + caseObjectId +
                ", commentId=" + commentId +
                ", personId=" + personId +
                '}';
    }

}
