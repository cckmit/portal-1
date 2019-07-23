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
    private Collection<Attachment> addedAttachments;
    private Collection<Attachment> removedAttachments;
    private Person person;

    private CaseAttachmentEvent(
            Object source, ServiceModule serviceModule,
            Person person, CaseObject caseObject,
            Collection<Attachment> addedAttachments, Collection<Attachment> removedAttachments
    ) {
        super(source);
        this.serviceModule = serviceModule;
        this.person = person;
        this.caseObject = caseObject;
        this.addedAttachments = addedAttachments;
        this.removedAttachments = removedAttachments;
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

    public ServiceModule getServiceModule() {
        return serviceModule;
    }

    public CaseObject getNewState() { return caseObject; }

    public CaseObject getOldState() { return caseObject; }

    public CaseComment getCaseComment() { return null; }

    public CaseComment getOldCaseComment() { return null; }

    public CaseComment getRemovedCaseComment() { return null; }


    public static class Builder {

        private Object source;
        private ServiceModule serviceModule;
        private CaseObject caseObject;
        private Collection<Attachment> addedAttachments;
        private Collection<Attachment> removedAttachments;
        private Person person;

        public Builder(Object source) {
            this.source = source;
            this.serviceModule = ServiceModule.GENERAL;
        }

        public Builder(Object source, ServiceModule serviceModule) {
            this.source = source;
            this.serviceModule = serviceModule;
        }

        public Builder withCaseObject(CaseObject caseObject) {
            this.caseObject = caseObject;
            return this;
        }

        public Builder withAddedAttachments(Collection<Attachment> addedAttachments) {
            this.addedAttachments = addedAttachments;
            return this;
        }

        public Builder withRemovedAttachments(Collection<Attachment> removedAttachments) {
            this.removedAttachments = removedAttachments;
            return this;
        }

        public Builder withPerson(Person person) {
            this.person = person;
            return this;
        }

        public CaseAttachmentEvent build() {
            return new CaseAttachmentEvent(
                    source,
                    serviceModule,
                    person,
                    caseObject,
                    addedAttachments,
                    removedAttachments
            );
        }
    }
}
