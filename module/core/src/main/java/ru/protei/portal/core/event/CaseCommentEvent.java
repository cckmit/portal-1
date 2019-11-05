package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.Person;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by michael on 04.05.17.
 */
public class CaseCommentEvent extends ApplicationEvent implements AbstractCaseEvent {

    private Long caseObjectId;
    private CaseComment newCaseComment;
    private CaseComment oldCaseComment;
    private CaseComment removedCaseComment;
    private Person person;
    private ServiceModule serviceModule;
    private Collection<Attachment> addedAttachments;
    private Collection<Attachment> removedAttachments;
    private boolean isEagerEvent;

    public CaseCommentEvent(Object source, ServiceModule serviceModule, Person person, Long caseObjectId, boolean isEagerEvent) {
        super(source);
        this.serviceModule = serviceModule;
        this.person = person;
        this.caseObjectId = caseObjectId;
        this.isEagerEvent = isEagerEvent;
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

    public Person getPerson() {
        return person;
    }

    public Collection<Attachment> getAddedAttachments() {
        return addedAttachments == null? Collections.emptyList(): addedAttachments;
    }

    public Collection<Attachment> getRemovedAttachments() {
        return removedAttachments == null? Collections.emptyList(): removedAttachments;
    }

    public CaseCommentEvent withNewCaseComment( CaseComment caseComment) {
        this.newCaseComment = caseComment;
        return this;
    }

    public CaseCommentEvent withOldCaseComment(CaseComment oldCaseComment) {
        this.oldCaseComment = oldCaseComment;
        return this;
    }

    public CaseCommentEvent withRemovedCaseComment(CaseComment removedCaseComment) {
        this.removedCaseComment = removedCaseComment;
        return this;
    }

    public CaseCommentEvent withAddedAttachments(Collection<Attachment> addedAttachments) {
        this.addedAttachments = addedAttachments;
        return this;
    }

    public CaseCommentEvent withRemovedAttachments(Collection<Attachment> removedAttachments) {
        this.removedAttachments = removedAttachments;
        return this;
    }


}
