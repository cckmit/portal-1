package ru.protei.portal.ui.common.client.activity.commenthistory;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_CaseCommentPrivacyType;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.timefield.HasTime;
import ru.protei.portal.ui.common.client.widget.uploader.impl.AttachmentUploader;

/**
 * Представление списка комментариев
 */
public interface AbstractCommentAndHistoryListView extends IsWidget {

    void setActivity( AbstractCommentAndHistoryListActivity activity );

    void clearItemsContainer();

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

    void setMentionEnabled(boolean isMentionEnabled);

    void setCommentPlaceholder(String placeholder);

    FlowPanel commentsAndHistoriesContainer();

    HasVisibility getPrivacyVisibility();

    void setIssueCommentHelpLink(String label, String link);

    boolean isDisplayPreview();

    void setTimeElapsedVisibility(boolean visible);

    boolean isAttached();

    void setPrivacyTypeSelector(boolean extendedPrivacyType);

    HasValue<En_CaseCommentPrivacyType> privacyType();

    void restyleFirstVisibleItemContainer();

    void setJiraWorkflowWarningVisible(boolean isVisible);
}
