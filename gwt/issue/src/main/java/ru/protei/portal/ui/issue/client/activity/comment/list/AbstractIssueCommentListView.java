package ru.protei.portal.ui.issue.client.activity.comment.list;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Представление списка комментариев
 */
public interface AbstractIssueCommentListView extends IsWidget {

    void setActivity( AbstractIssueCommentListActivity activity );

    HasWidgets getCommentsContainer();

    HasValue<String> message();

    void focus();
}
