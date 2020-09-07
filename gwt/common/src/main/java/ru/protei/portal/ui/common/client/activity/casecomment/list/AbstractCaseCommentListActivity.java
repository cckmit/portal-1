package ru.protei.portal.ui.common.client.activity.casecomment.list;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.ent.Attachment;

/**
 * Абстрактная активность списка комментариев
 */
public interface AbstractCaseCommentListActivity extends Activity {

    void onSendClicked();

    void onEditLastMessage();

    void removeTempAttachment(Attachment attachment);

    void onDetachView();

    void onCommentChanged(String text);

    void onDisplayPreviewChanged( Boolean isDisplayPreview );
}
