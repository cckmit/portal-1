package ru.protei.portal.ui.common.client.activity.casecomment.list;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.timefield.HasTime;
import ru.protei.portal.ui.common.client.widget.uploader.impl.AttachmentUploader;

/**
 * Представление списка комментариев
 */
public interface AbstractCaseCommentListView extends IsWidget {

    void setActivity( AbstractCaseCommentListActivity activity );

    void clearCommentsContainer();

    void addCommentToFront( IsWidget comment );

    void replaceCommentView( IsWidget removed, IsWidget inserted );

    void removeComment( IsWidget comment );

    HasValue<String> message();

    void focus();

    void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler);

    HasAttachments attachmentContainer();

    void setNewCommentHidden(boolean isHidden);

    void setNewCommentDisabled(boolean isDisabled);

    HasTime timeElapsed();

    HasValue<En_TimeElapsedType> timeElapsedType();

    void clearTimeElapsed();

    void setUserIcon(String icon);

    HasEnabled sendEnabled();

    void setPreviewText(String text);

    void setPreviewVisible(boolean isVisible);

    void setCaseCreatorId(Long personId);

    void setInitiatorCompanyId(Long initiatorCompanyId);

    void setCommentPlaceholder(String placeholder);

    HasVisibility getPrivacyVisibility();

    HasValue<Boolean> privateComment();

    void setMarkupLabel(String label, String link);

    boolean isDisplayPreview();

    void setTimeElapsedVisibility(boolean visible);

    boolean isAttached();
}
