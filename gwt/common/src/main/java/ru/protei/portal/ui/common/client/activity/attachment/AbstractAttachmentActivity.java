package ru.protei.portal.ui.common.client.activity.attachment;

import com.google.gwt.user.client.ui.Image;

/**
 * Активити вложения
 */
public interface AbstractAttachmentActivity {
    void onAttachmentRemove(AbstractAttachmentView attachment);
    void onShowPreview(Image attachment);
}
