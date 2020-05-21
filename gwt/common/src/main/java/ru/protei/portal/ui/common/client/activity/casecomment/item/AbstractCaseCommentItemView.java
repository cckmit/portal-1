package ru.protei.portal.ui.common.client.activity.casecomment.item;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;

import java.util.function.Consumer;

/**
 * Представление одного комментария
 */
public interface AbstractCaseCommentItemView extends IsWidget {

    void setActivity( AbstractCaseCommentItemActivity activity );

    void setTimeElapsedTypeChangeHandler(Consumer<ValueChangeEvent<En_TimeElapsedType>> editTimeElapsedType);

    void setDate(String value );

    void setOwner( String value );

    void setMessage(String value );

    void setMine();

    void setStatus( En_CaseState value );

    void setImportanceLevel( En_ImportanceLevel importance );

    void setManagerAndCompany(String managerShortName, String managerCompanyName );

    void enabledEdit( boolean isEnabled );

    void enableReply(boolean isEnabled);

    void enableUpdateTimeElapsedType(boolean isTimeElapsedTypeEnabled);

    void showAttachments(boolean isShow);

    HasAttachments attachmentContainer();

    void hideOptions();

    void setIcon( String iconSrc );

    void setTimeElapsed( String timeTypeString );

    void clearElapsedTime();

    void setRemoteLinkNumber(String number);

    void setRemoteLinkHref(String link);

    void setPrivacyFlag(Boolean value);

    void setTimeElapsedType(En_TimeElapsedType type);
}
