package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.CaseAttachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.helper.StringUtils;

import static ru.protei.portal.core.model.helper.CollectionUtils.toList;

/**
 * Created by michael on 04.05.17.
 */
public class CaseCommentEvent extends ApplicationEvent implements AbstractCaseEvent {

    private Long caseObjectId;
    private CaseComment newCaseComment;
    private CaseComment oldCaseComment;
    private CaseComment removedCaseComment;
    private Long personId;
    private ServiceModule serviceModule;
    private boolean isEagerEvent;

    public CaseCommentEvent(Object source, ServiceModule serviceModule, Long personId, Long caseObjectId, boolean isEagerEvent,
                            CaseComment oldCaseComment, CaseComment newCaseComment, CaseComment removedCaseComment
                            ) {
        super(source);
        this.serviceModule = serviceModule;
        this.personId = personId;
        this.caseObjectId = caseObjectId;
        this.isEagerEvent = isEagerEvent;
        this.oldCaseComment = oldCaseComment;
        this.newCaseComment = newCaseComment;
        this.removedCaseComment = removedCaseComment;
    }

    public ServiceModule getServiceModule() {
        return serviceModule;
    }

    public Long getCaseObjectId(){
        return caseObjectId;
    }

    @Override
    public boolean isEagerEvent() {
        return isEagerEvent;
    }

    public CaseComment getNewCaseComment() {
        return newCaseComment;
    }

    public CaseComment getOldCaseComment() {
        return oldCaseComment;
    }

    public CaseComment getRemovedCaseComment() {
        return removedCaseComment;
    }

    @Override
    public Long getPersonId() {
        return personId;
    }

    @Override
    public String toString() {
        return "CaseCommentEvent{" +
                "caseObjectId=" + caseObjectId +
                ", isEagerEvent=" + isEagerEvent() +
                ", personId=" + personId +
                ", isEagerEvent=" + isEagerEvent +
                ", newCaseComment=" + asString( newCaseComment) +
                ", oldCaseComment=" + asString(oldCaseComment) +
                ", removedCaseComment=" + asString(removedCaseComment) +
                '}';
    }

    private String asString( CaseComment caseComment ) {
        if (caseComment == null) return "null";
        return "{" +
                "id=" + caseComment.getId() +
                ", caseId=" + caseComment.getCaseId() +
                ", privacyType=" + caseComment.getPrivacyType() +
                ", text length='" + StringUtils.length( caseComment.getText() ) + '\'' +
                ", timeElapsedType=" + caseComment.getTimeElapsedType() +
                ", timeElapsed=" + caseComment.getTimeElapsed() +
                ", remoteLink=" + caseComment.getRemoteLink() +
                ", caseAttachments=" + toList( caseComment.getCaseAttachments(), CaseAttachment::getId ) +
                '}';
    }
}
