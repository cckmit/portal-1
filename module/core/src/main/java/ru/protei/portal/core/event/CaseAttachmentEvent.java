package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.util.DiffCollectionResult;

import java.util.Collection;

import static ru.protei.portal.core.model.helper.CollectionUtils.toList;

public class CaseAttachmentEvent extends ApplicationEvent implements AbstractCaseEvent {

    private final ServiceModule serviceModule;
    private DiffCollectionResult<Attachment> attachments = new DiffCollectionResult<>();
    private Long personId;
    private Long caseObjectId;

    public CaseAttachmentEvent(
            Object source, ServiceModule serviceModule,
            Long personId,
            Long caseObjectId,
            Collection<Attachment> addedAttachments,
            Collection<Attachment> removedAttachments
    ) {
        super(source);
        this.serviceModule = serviceModule;
        this.personId = personId;
        this.caseObjectId = caseObjectId;

        attachments.putAddedEntries( addedAttachments );
        attachments.putRemovedEntries( removedAttachments  );
    }

    public DiffCollectionResult<Attachment> getAttachments() {
        return attachments;
    }

    @Override
    public Long getPersonId() {
        return personId;
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
                ", personId=" + personId +
//                ", oldAttachments=" + toList(oldAttachments, Attachment::getId) +
                ", addedAttachments=" + toList(attachments.getAddedEntries(), Attachment::getId) +
                ", removedAttachments=" + toList(attachments.getRemovedEntries(), Attachment::getId )+
                '}';
    }
}
