package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.util.DiffCollectionResult;

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
    private DiffCollectionResult<CaseLink> mergeLinks;

    private CaseObjectCommentEvent(Object source){
        super(source);
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

    public DiffCollectionResult<CaseLink> getMergeLinks() {
        return mergeLinks;
    }

    public static CaseObjectCommentEvent create( Object source) {
        return create(source, ServiceModule.GENERAL);
    }

    public static CaseObjectCommentEvent create( Object source, ServiceModule serviceModule) {
        CaseObjectCommentEvent event = new CaseObjectCommentEvent( source );
        event.serviceModule = serviceModule;
        return event;
    }

    public CaseObjectCommentEvent withPerson( Person person) {
        this.person = person;
        return this;
    }

    public CaseObjectCommentEvent withNewState( CaseObject newState) {
        this.newState = newState;
        return this;
    }

    public CaseObjectCommentEvent withOldState( CaseObject oldState) {
        this.oldState = oldState;
        return this;
    }

    public CaseObjectCommentEvent withCaseComment( CaseComment caseComment) {
        this.caseComment = caseComment;
        return this;
    }

    public CaseObjectCommentEvent withOldCaseComment( CaseComment oldCaseComment) {
        this.oldCaseComment = oldCaseComment;
        return this;
    }

    public CaseObjectCommentEvent withRemovedCaseComment( CaseComment removedCaseComment) {
        this.removedCaseComment = removedCaseComment;
        return this;
    }

    public CaseObjectCommentEvent withAddedAttachments( Collection<Attachment> addedAttachments) {
        this.addedAttachments = addedAttachments;
        return this;
    }

    public CaseObjectCommentEvent withRemovedAttachments( Collection<Attachment> removedAttachments) {
        this.removedAttachments = removedAttachments;
        return this;
    }

    public CaseObjectCommentEvent withLinks( DiffCollectionResult<CaseLink> mergeLinks ) {
        this.mergeLinks = mergeLinks;
        return this;
    }

}
