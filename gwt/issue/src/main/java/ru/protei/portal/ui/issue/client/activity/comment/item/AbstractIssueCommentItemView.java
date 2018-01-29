package ru.protei.portal.ui.issue.client.activity.comment.item;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;

/**
 * Представление одного комментария
 */
public interface AbstractIssueCommentItemView extends IsWidget {

    void setActivity( AbstractIssueCommentItemActivity activity );

    void setDate( String value );

    void setOwner( String value );

    void setMessage( String value );

    void setMine();

    void setStatus( En_CaseState value );

    void enabledEdit( boolean isEnabled );

    void showAttachments(boolean isShow);

    HasAttachments attachmentContainer();

    void hideOptions();

    void setIcon( String iconSrc );
}
