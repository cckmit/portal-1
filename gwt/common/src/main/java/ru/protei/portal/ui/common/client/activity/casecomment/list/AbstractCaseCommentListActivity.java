package ru.protei.portal.ui.common.client.activity.casecomment.list;

import ru.protei.portal.core.model.ent.Attachment;

/**
 * Абстрактная активность списка комментариев
 */
public interface AbstractCaseCommentListActivity {

    void onSendClicked();

    void onEditLastMessage();

    void removeTempAttachment(Attachment attachment);

    void onDetachView();
}
