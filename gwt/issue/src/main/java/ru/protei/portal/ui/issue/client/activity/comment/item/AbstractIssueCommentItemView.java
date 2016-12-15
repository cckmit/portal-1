package ru.protei.portal.ui.issue.client.activity.comment.item;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Представление одного комментария
 */
public interface AbstractIssueCommentItemView extends IsWidget {

    void setActivity( AbstractIssueCommentItemActivity activity );

    void setDate( String value );

    void setOwner( String value );

    void setMessage( String value );

    void setMine();

    void enabledEdit( boolean isEnabled );
}
