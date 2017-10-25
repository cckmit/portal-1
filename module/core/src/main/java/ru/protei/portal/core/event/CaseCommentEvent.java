package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.service.CaseService;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by michael on 04.05.17.
 */
public class CaseCommentEvent extends ApplicationEvent {

    private CaseObject caseObject;
    private CaseComment caseComment;
    private CaseComment oldCaseComment;
    private Person person;
    private ServiceModule serviceModule;
    private Collection<Attachment> addedAttachments;
    private Collection<Attachment> removedAttachments;

    public CaseCommentEvent(CaseService source, CaseObject caseObject, CaseComment comment, Collection<Attachment> attachments, Person currentPerson) {
        this(ServiceModule.GENERAL, source, caseObject, null, null, comment, attachments, currentPerson);
    }

    public CaseCommentEvent(
            CaseService source,
            CaseObject caseObject,
            CaseComment oldComment, Collection<Attachment> removedAttachments,
            CaseComment comment, Collection<Attachment> addedAttachments,
            Person currentPerson) {
        this(ServiceModule.GENERAL, source, caseObject, oldComment, removedAttachments, comment, addedAttachments, currentPerson);
    }

    public CaseCommentEvent(
            ServiceModule serviceModule,
            CaseService source,
            CaseObject caseObject,
            CaseComment oldComment, Collection<Attachment> removedAttachments,
            CaseComment comment, Collection<Attachment> addedAttachments,
            Person currentPerson) {
        super(source);
        this.caseObject = caseObject;
        this.caseComment = comment;
        this.oldCaseComment = oldComment;
        this.person = currentPerson;
        this.serviceModule = serviceModule;
        this.removedAttachments = removedAttachments;
        this.addedAttachments = addedAttachments;
    }

    public ServiceModule getServiceModule() {
        return serviceModule;
    }

    public CaseObject getCaseObject() {
        return caseObject;
    }

    public CaseComment getCaseComment() {
        return caseComment;
    }

    public CaseComment getOldCaseComment() {
        return oldCaseComment;
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
}
