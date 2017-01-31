package ru.protei.portal.ui.common.client.common;

import ru.protei.portal.core.model.ent.Attachment;

import java.util.HashMap;

/**
 * Коллекция для работы с вложениями
 */
public abstract class AttachmentCollection extends HashMap<Long, Attachment> {

    public abstract void addAttachment(Attachment attachment);

    public abstract void removeAttachment(Attachment attachment);

}