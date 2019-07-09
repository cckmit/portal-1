package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;

import java.util.Collection;
import java.util.Collections;

public class CaseObjectCommentEvent extends ApplicationEvent implements AbstractCaseEvent {

    private ServiceModule serviceModule;
    private Person person;
    private CaseObject newState;
    private CaseObject oldState;
    private CaseComment caseComment;
    private CaseComment oldCaseComment;
    private CaseComment removedCaseComment;
    private Collection<Attachment> addedAttachments;
    private Collection<Attachment> removedAttachments;

    private CaseObjectCommentEvent(
            Object source, ServiceModule serviceModule,
            Person person, CaseObject newState, CaseObject oldState,
            CaseComment caseComment, CaseComment oldCaseComment, CaseComment removedCaseComment,
            Collection<Attachment> addedAttachments, Collection<Attachment> removedAttachments
    ) {
        super(source);
        this.serviceModule = serviceModule;
        this.person = person;
        this.newState = newState;
        this.oldState = oldState;
        this.caseComment = caseComment;
        this.oldCaseComment = oldCaseComment;
        this.removedCaseComment = removedCaseComment;
        this.addedAttachments = addedAttachments;
        this.removedAttachments = removedAttachments;
    }

    public ServiceModule getServiceModule() {
        return serviceModule;
    }

    public Person getPerson() {
        return person;
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

    public Collection<Attachment> getAddedAttachments() {
        return addedAttachments == null ? Collections.emptyList() : addedAttachments;
    }

    public Collection<Attachment> getRemovedAttachments() {
        return removedAttachments == null ? Collections.emptyList() : removedAttachments;
    }

    public static class Builder {

        private Object source;
        private ServiceModule serviceModule;
        private Person person;
        private CaseObject newState;
        private CaseObject oldState;
        private CaseComment caseComment;
        private CaseComment oldCaseComment;
        private CaseComment removedCaseComment;
        private Collection<Attachment> addedAttachments;
        private Collection<Attachment> removedAttachments;

        public Builder(Object source) {
            this.source = source;
            this.serviceModule = ServiceModule.GENERAL;
        }

        public Builder(Object source, ServiceModule serviceModule) {
            this.source = source;
            this.serviceModule = serviceModule;
        }

        public Builder withPerson(Person person) {
            this.person = person;
            return this;
        }

        public Builder withState(CaseObject state) {
            this.newState = state;
            this.oldState = state;
            return this;
        }

        public Builder withNewState(CaseObject newState) {
            this.newState = newState;
            return this;
        }

        public Builder withOldState(CaseObject oldState) {
            this.oldState = oldState;
            return this;
        }

        public Builder withCaseComment(CaseComment caseComment) {
            this.caseComment = caseComment;
            return this;
        }

        public Builder withOldCaseComment(CaseComment oldCaseComment) {
            this.oldCaseComment = oldCaseComment;
            return this;
        }

        public Builder withRemovedCaseComment(CaseComment removedCaseComment) {
            this.removedCaseComment = removedCaseComment;
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

        public CaseObjectCommentEvent build() {
            return new CaseObjectCommentEvent(
                    source,
                    serviceModule,
                    person,
                    newState,
                    oldState,
                    caseComment,
                    oldCaseComment,
                    removedCaseComment,
                    addedAttachments,
                    removedAttachments
            );
        }
    }
}
