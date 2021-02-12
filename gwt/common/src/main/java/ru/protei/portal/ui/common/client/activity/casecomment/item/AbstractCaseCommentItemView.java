package ru.protei.portal.ui.common.client.activity.casecomment.item;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseCommentPrivacyType;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
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

    void setStatus( String value, String color );

    void setImportanceLevel( String importanceCode, String color );

    void setManagerInfo(String managerInfo );

    void enabledEdit( boolean isEnabled );

    void enableReply(boolean isEnabled);

    void showAttachments(boolean isShow);

    HasAttachments attachmentContainer();

    void hideOptions();

    void setIcon( String iconSrc );

    void setTimeElapsed( String timeTypeString );

    void clearElapsedTime();

    void setRemoteLinkNumber(String number);

    void setRemoteLinkHref(String link);

    void setPrivacyType(En_CaseCommentPrivacyType value);

    void setTimeElapsedType(En_TimeElapsedType type);

    void displayUpdatedAnimation();

    void displayAddedAnimation();

    HasVisibility timeElapsedTypePopupVisibility();
}
