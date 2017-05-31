package ru.protei.portal.ui.issue.client.activity.comment.item;

import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.ui.issue.client.view.comment.item.IssueCommentItemView;

/**
 * Абстрактная активность одного комментария
 */
public interface AbstractIssueCommentItemActivity {

    void onRemoveClicked( AbstractIssueCommentItemView itemView );

    void onEditClicked( AbstractIssueCommentItemView itemView );

    void onReplyClicked( AbstractIssueCommentItemView itemView );

    void onRemoveAttachment(IssueCommentItemView view, Attachment attachment);

}
