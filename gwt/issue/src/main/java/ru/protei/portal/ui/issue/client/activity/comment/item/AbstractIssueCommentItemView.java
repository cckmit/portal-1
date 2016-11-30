package ru.protei.portal.ui.issue.client.activity.comment.item;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.CaseComment;

/**
 * Представление одного комментария
 */
public interface AbstractIssueCommentItemView extends IsWidget {

    void setActivity( AbstractIssueCommentItemActivity activity );

    void setValue( CaseComment value );
}
