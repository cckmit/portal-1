package ru.protei.portal.ui.issue.client.activity.comment.list;

import ru.protei.portal.core.model.ent.Attachment;

/**
 * Абстрактная активность списка комментариев
 */
public interface AbstractIssueCommentListActivity {

    void onSendClicked();

    void onEditLastMessage();

    void removeTempAttachment(Attachment attachment);

    void onDetachView();
}
