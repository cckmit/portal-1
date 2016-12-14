package ru.protei.portal.ui.issue.client.activity.comment.list;

import com.google.gwt.user.client.ui.*;

/**
 * Представление списка комментариев
 */
public interface AbstractIssueCommentListView extends IsWidget {

    void setActivity( AbstractIssueCommentListActivity activity );

    HasWidgets getCommentsContainer();

    HasValue<String> message();

    void focus();
}
