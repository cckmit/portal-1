package ru.protei.portal.ui.issue.client.activity.comment.item;

/**
 * Абстрактная активность одного комментария
 */
public interface AbstractIssueCommentItemActivity {

    void onRemoveClicked( AbstractIssueCommentItemView itemView );

    void onEditClicked( AbstractIssueCommentItemView itemView );

    void onReplyClicked( AbstractIssueCommentItemView itemView );
}
