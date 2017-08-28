package ru.protei.portal.ui.common.client.events;

import ru.protei.portal.core.model.ent.Attachment;

import java.util.Collection;


public class AttachmentEvents {

    public static class Remove{
        public Long caseId;
        public Collection<Attachment> attachments;

        public Remove(Long caseId, Collection<Attachment> attachments) {
            this.caseId = caseId;
            this.attachments = attachments;
        }
    }

    public static class Add{
        public Long caseId;
        public Collection<Attachment> attachments;

        public Add(Long caseId, Collection<Attachment> attachments) {
            this.caseId = caseId;
            this.attachments = attachments;
        }
    }
}
