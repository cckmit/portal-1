package ru.protei.portal.ui.issue.client.activity.preview;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueDetailsView;

/**
 * Абстракция вида превью обращения
 */
public interface AbstractIssuePreviewView extends IsWidget, AbstractIssueDetailsView {

    void setActivity(AbstractIssuePreviewActivity activity);

    void setFullScreen( boolean isFullScreen);

    void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler);

    HasVisibility backBtnVisibility();

}
