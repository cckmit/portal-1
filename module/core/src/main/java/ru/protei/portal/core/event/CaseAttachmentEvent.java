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
    private Collection<Attachment> oldAttachments;
    private Collection<Attachment> addedAttachments;
    private Collection<Attachment> removedAttachments;
    private Person person;
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
        return false;
    }

    public ServiceModule getServiceModule() {
        return serviceModule;
    }

    public CaseComment getCaseComment() { return null; }

    public CaseAttachmentEvent withAddedAttachments(Collection<Attachment> addedAttachments) {
        this.addedAttachments = addedAttachments;
        return this;
    }

    public CaseAttachmentEvent withRemovedAttachments(Collection<Attachment> removedAttachments) {
        this.removedAttachments = removedAttachments;
        return this;
    }

}
