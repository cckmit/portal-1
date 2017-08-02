package ru.protei.portal.ui.issue.client.activity.comment.list;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.uploader.FileUploader;

/**
 * Представление списка комментариев
 */
public interface AbstractIssueCommentListView extends IsWidget {

    void setActivity( AbstractIssueCommentListActivity activity );

    HasWidgets getCommentsContainer();

    HasValue<String> message();

    void focus();

    void setFileUploadHandler(FileUploader.FileUploadHandler handler);

    HasAttachments attachmentContainer();

    void enabledNewComment( boolean value );
}
