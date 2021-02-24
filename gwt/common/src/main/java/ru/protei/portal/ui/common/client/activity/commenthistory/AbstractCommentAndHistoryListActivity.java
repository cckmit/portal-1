package ru.protei.portal.ui.common.client.activity.commenthistory;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.ent.Attachment;

/**
 * Абстрактная активность списка комментариев
 */
public interface AbstractCommentAndHistoryListActivity extends Activity {

    void onSendClicked();

    void removeTempAttachment(Attachment attachment);

    void onDetachView();

    void onCommentChanged(String text);

    void onDisplayPreviewChanged( Boolean isDisplayPreview );

    void onAddingCommentHelpLinkClicked();
}
