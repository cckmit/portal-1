package ru.protei.portal.ui.common.client.activity.casecomment.item;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.timefield.HasTime;

/**
 * Представление одного комментария
 */
public interface AbstractCaseCommentItemView extends IsWidget {

    void setActivity( AbstractCaseCommentItemActivity activity );

    void setDate( String value );

    void setOwner( String value );

    void setMessage(String value );

    void setMine();

    void setStatus( En_CaseState value );

    void setImportanceLevel( En_ImportanceLevel importance );

    void enabledEdit( boolean isEnabled );

    void enableReply(boolean isEnabled);

    void showAttachments(boolean isShow);

    HasAttachments attachmentContainer();

    void hideOptions();

    void setIcon( String iconSrc );

    HasTime timeElapsed();

    void clearElapsedTime();

    void setRemoteLink(CaseLink remoteLink);
}
