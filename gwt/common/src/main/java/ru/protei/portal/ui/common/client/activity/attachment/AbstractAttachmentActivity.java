package ru.protei.portal.ui.common.client.activity.attachment;

import com.google.gwt.user.client.ui.Image;
import ru.brainworm.factory.generator.activity.client.activity.Activity;

/**
 * Активити вложения
 */
public interface AbstractAttachmentActivity {
    void onAttachmentRemove(AbstractAttachmentView attachment);
    void onShowPreview(Image attachment);
    void setActivity(Activity activity);
}
