package ru.protei.portal.ui.common.client.activity.casecomment.item;

import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.ui.common.client.view.casecomment.item.CaseCommentItemView;

/**
 * Абстрактная активность одного комментария
 */
public interface AbstractCaseCommentItemActivity {

    void onRemoveClicked( AbstractCaseCommentItemView itemView );

    void onEditClicked( AbstractCaseCommentItemView itemView );

    void onReplyClicked( AbstractCaseCommentItemView itemView );

    void onRemoveAttachment(CaseCommentItemView view, Attachment attachment);

    void onTimeElapsedClicked(AbstractCaseCommentItemView view);
}
