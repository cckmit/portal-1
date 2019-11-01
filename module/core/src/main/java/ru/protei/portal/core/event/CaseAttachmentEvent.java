package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;

import java.util.Collection;
import java.util.Collections;

public class CaseAttachmentEvent extends ApplicationEvent implements AbstractCaseEvent {

    private final ServiceModule serviceModule;
    private CaseObject caseObject;
    private Collection<Attachment> oldAttachments;
    private Collection<Attachment> addedAttachments;
    private Collection<Attachment> removedAttachments;
    private Person person;
    private boolean isEagerEvent;
    private Long caseObjectId;

    public CaseAttachmentEvent(
            Object source, ServiceModule serviceModule,
            Person person,
            Long caseObjectId,
            Collection<Attachment> oldAttachments

    ) {
        super(source);
        this.serviceModule = serviceModule;
        this.person = person;
        this.caseObjectId = caseObjectId;
        this.oldAttachments = oldAttachments;
    }

    public CaseObject getCaseObject() {
        return caseObject;
    }

    public Collection<Attachment> getAddedAttachments() {
        return addedAttachments == null? Collections.emptyList(): addedAttachments;
    }

    public Collection<Attachment> getRemovedAttachments() {
        return removedAttachments == null? Collections.emptyList(): removedAttachments;
    }

    public Person getPerson() {
        return person;
    }

    @Override
    public Long getCaseObjectId() {
        return caseObjectId;
    }

    @Override
    public boolean isEagerEvent() {
        return isEagerEvent;
    }

    public ServiceModule getServiceModule() {
        return serviceModule;
    }

    public CaseObject getNewState() { return caseObject; }

    public CaseObject getOldState() { return caseObject; }

    public CaseComment getCaseComment() { return null; }

    public CaseComment getOldCaseComment() { return null; }

    public CaseComment getRemovedCaseComment() { return null; }


//    public CaseAttachmentEvent withCaseObject(CaseObject caseObject) {
//        this.caseObject = caseObject;
//        return this;
//    }

    public CaseAttachmentEvent withAddedAttachments(Collection<Attachment> addedAttachments) {
        this.addedAttachments = addedAttachments;
        return this;
    }

    public CaseAttachmentEvent withRemovedAttachments(Collection<Attachment> removedAttachments) {
        this.removedAttachments = removedAttachments;
        return this;
    }

//    public CaseAttachmentEvent withPerson(Person person) {
//        this.person = person;
//        return this;
//    }

}
