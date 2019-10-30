package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.service.CaseServiceImpl;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by michael on 04.05.17.
 */
public class CaseCommentEvent extends ApplicationEvent implements AbstractCaseEvent {

    private CaseObject newState;
    private CaseObject oldState;
    private CaseComment caseComment;
    private CaseComment oldCaseComment;
    private CaseComment removedCaseComment;
    private Person person;
    private ServiceModule serviceModule;
    private Collection<Attachment> addedAttachments;
    private Collection<Attachment> removedAttachments;

    private CaseCommentEvent(Object source) {
        super(source);
    }

    public ServiceModule getServiceModule() {
        return serviceModule;
    }

    public CaseObject getCaseObject() {
        return newState != null ? newState : oldState;
    }

    public CaseObject getNewState() {
        return newState;
    }

    public CaseObject getOldState() {
        return oldState;
    }

    public CaseComment getCaseComment() {
        return caseComment;
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

    public static CaseCommentEvent create( Object source ) {
        return create(source, ServiceModule.GENERAL);
    }

    public static CaseCommentEvent create( Object source, ServiceModule serviceModule ) {
        CaseCommentEvent event = new CaseCommentEvent(source);
        event.serviceModule = serviceModule;
        return event;
    }

    public CaseCommentEvent withPerson(Person person) {
        this.person = person;
        return this;
    }

    public CaseCommentEvent withNewState(CaseObject newState) {
        this.newState = newState;
        return this;
    }

    public CaseCommentEvent withOldState(CaseObject oldState) {
        this.oldState = oldState;
        return this;
    }

    public CaseCommentEvent withCaseComment(CaseComment caseComment) {
        this.caseComment = caseComment;
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
