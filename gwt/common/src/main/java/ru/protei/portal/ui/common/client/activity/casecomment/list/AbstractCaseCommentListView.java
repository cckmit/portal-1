package ru.protei.portal.ui.common.client.activity.casecomment.list;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.timefield.HasTime;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;

/**
 * Представление списка комментариев
 */
public interface AbstractCaseCommentListView extends IsWidget {

    void setActivity( AbstractCaseCommentListActivity activity );

    void clearCommentsContainer();

    void addCommentToFront( IsWidget comment );

    void removeComment( IsWidget comment );

    HasValue<String> message();

    void focus();

    void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler);

    HasAttachments attachmentContainer();

    void enabledNewComment( boolean value );

    HasTime timeElapsed();

    HasValue<En_TimeElapsedType> timeElapsedType();

    void clearTimeElapsed();

    HasVisibility timeElapsedVisibility();

    HasVisibility timeElapsedTypeVisibility();

    void setUserIcon(String icon);

    HasEnabled sendEnabled();

    void setPreviewText(String text);

    void setPreviewVisible(boolean isVisible);

    HasVisibility getPrivacyVisibility();

    HasValue<Boolean> privateComment();

    void setTextMarkupLabel(String label);
}
