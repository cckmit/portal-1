package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Attachment;

public class CaseAttachmentEvent extends ApplicationEvent {

    private Attachment attachment;
    private Long caseId;

    public CaseAttachmentEvent(Object source, Attachment attachment, Long caseId) {
        super(source);
        this.attachment = attachment;
        this.caseId = caseId;
    }


    public Attachment getAttachment() {
        return attachment;
    }

    public Long getCaseId() {
        return caseId;
    }
}
