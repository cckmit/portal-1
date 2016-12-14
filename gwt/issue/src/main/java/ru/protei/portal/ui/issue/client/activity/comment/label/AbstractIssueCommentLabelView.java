package ru.protei.portal.ui.issue.client.activity.comment.label;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseState;

/**
 * Представление одного комментария
 */
public interface AbstractIssueCommentLabelView extends IsWidget {

    void setActivity( AbstractIssueCommentLabelActivity activity );

    void setDate( String value );

    void setOwner( String value );

    void setStatus( En_CaseState value );
}
