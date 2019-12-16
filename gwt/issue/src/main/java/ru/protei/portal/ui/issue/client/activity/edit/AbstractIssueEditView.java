package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;

/**
 * Представление создания и редактирования обращения
 */
public interface AbstractIssueEditView extends IsWidget, AbstractIssueDetailsView {

    void setActivity( AbstractIssueEditActivity activity );

    void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler);

    boolean isAttached();

    String DESCRIPTION = "description";
}
