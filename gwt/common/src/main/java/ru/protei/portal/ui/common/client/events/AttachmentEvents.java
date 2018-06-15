package ru.protei.portal.ui.common.client.events;

import ru.protei.portal.core.model.ent.Attachment;

import java.util.Collection;


public class AttachmentEvents {

    public static class Remove{
        public Long issueId;
        public Collection<Attachment> attachments;

        public Remove(Long issueId, Collection<Attachment> attachments) {
            this.issueId = issueId;
            this.attachments = attachments;
        }
    }

    public static class Add{
        public Long issueId;
        public Collection<Attachment> attachments;

        public Add(Long issueId, Collection<Attachment> attachments) {
            this.issueId = issueId;
            this.attachments = attachments;
        }
    }
}
