package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.service.CaseService;

import java.util.Collection;
import java.util.Collections;

public class CaseAttachmentEvent extends ApplicationEvent {

    private final ServiceModule serviceModule;
    private CaseObject caseObject;
    private Collection<Attachment> addedAttachments;
    private Collection<Attachment> removedAttachments;
    private Person person;

    public CaseAttachmentEvent(ServiceModule module, Object source, CaseObject caseObject,
                               Collection<Attachment> addedAttachments, Collection<Attachment> removedAttachments,
                               Person person) {
        super(source);
        this.serviceModule = module;
        this.caseObject = caseObject;
        this.addedAttachments = addedAttachments;
        this.removedAttachments = removedAttachments;
        this.person = person;
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
}
