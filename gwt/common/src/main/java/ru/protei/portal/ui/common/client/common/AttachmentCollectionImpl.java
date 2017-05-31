package ru.protei.portal.ui.common.client.common;

import ru.protei.portal.core.model.ent.Attachment;

/**
 * Created by bondarenko on 25.01.17.
 */
public class AttachmentCollectionImpl extends AttachmentCollection {
    @Override
    public void addAttachment(Attachment attachment) {
        if(attachment != null)
            put(attachment.getId(), attachment);
    }

    @Override
    public void removeAttachment(Attachment attachment) {
        if(attachment != null)
            remove(attachment.getId());
    }
}
