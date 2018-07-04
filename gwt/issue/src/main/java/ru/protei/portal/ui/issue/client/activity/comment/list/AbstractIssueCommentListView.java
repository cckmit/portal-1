package ru.protei.portal.ui.issue.client.activity.comment.list;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.timefield.HasTime;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;

/**
 * Представление списка комментариев
 */
public interface AbstractIssueCommentListView extends IsWidget {

    void setActivity( AbstractIssueCommentListActivity activity );

    void clearCommentsContainer();

    void addCommentToFront( IsWidget comment );

    void removeComment( IsWidget comment );

    HasValue<String> message();

    void focus();

    void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler);

    HasAttachments attachmentContainer();

    void enabledNewComment( boolean value );

    HasTime timeElapsed();

    void clearTimeElapsed();

    HasVisibility timeElapsedVisibility();
}
