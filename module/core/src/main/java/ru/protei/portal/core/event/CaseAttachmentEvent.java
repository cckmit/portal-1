package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.util.DiffCollectionResult;

import java.util.Collection;
import java.util.Collections;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;
import static ru.protei.portal.core.model.helper.CollectionUtils.toList;

public class CaseAttachmentEvent extends ApplicationEvent implements AbstractCaseEvent {

    private final ServiceModule serviceModule;
    private DiffCollectionResult<Attachment> attachments = new DiffCollectionResult<>();
    private Person person;
    private Long caseObjectId;

    public CaseAttachmentEvent(
            Object source, ServiceModule serviceModule,
            Person person,
            Long caseObjectId,
            Collection<Attachment> addedAttachments,
            Collection<Attachment> removedAttachments
    ) {
        super(source);
        this.serviceModule = serviceModule;
        this.person = person;
        this.caseObjectId = caseObjectId;

        attachments.putAddedEntries( addedAttachments );
        attachments.putRemovedEntries( removedAttachments  );
    }

    public DiffCollectionResult<Attachment> getAttachments() {
        return attachments;
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

    @Override
    public String toString() {
        return "CaseAttachmentEvent{" +
                "caseObjectId=" + caseObjectId +
                ", isEagerEvent=" + isEagerEvent() +
                ", person=" + (person==null?null:person.getId()) +
//                ", oldAttachments=" + toList(oldAttachments, Attachment::getId) +
                ", addedAttachments=" + toList(attachments.getAddedEntries(), Attachment::getId) +
                ", removedAttachments=" + toList(attachments.getRemovedEntries(), Attachment::getId )+
                '}';
    }
}
