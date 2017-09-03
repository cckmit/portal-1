package ru.protei.portal.ui.official.client.activity.preview;

import ru.protei.portal.core.model.ent.Attachment; /**
 * Абстрактная активность карточки должностных лиц
 */
public interface AbstractOfficialPreviewActivity {

    void onFullScreenClicked();

    void onAddCLicked();

    void removeAttachment(Attachment attachment);
}
