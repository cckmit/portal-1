package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.widget.uploader.AbstractAttachmentUploader;
import ru.protei.portal.ui.common.client.widget.uploader.impl.AttachmentUploader;

/**
 * Представление создания и редактирования обращения
 */
public interface AbstractIssueEditView extends IsWidget {

    void setActivity(AbstractIssueEditActivity activity);

    void setPreviewStyles(boolean isPreview);

    void setCaseNumber(Long caseNumber);

    void setPrivateIssue(boolean privateIssue);

    void setCreatedBy(String value);

    void setName(String issueName);

    void setIntegration(String name);

    HasWidgets getTagsContainer();

    HasWidgets getInfoContainer();

    HasWidgets getMetaContainer();

    HasWidgets getLinksContainer();

    HasVisibility nameVisibility();

    HasVisibility backButtonVisibility();

    HasVisibility showEditViewButtonVisibility();

    HasVisibility nameAndDescriptionEditButtonVisibility();

    HasVisibility addTagButtonVisibility();

    HasVisibility addLinkButtonVisibility();

    boolean isAttached();

    String DESCRIPTION = "description";

    void setFavoriteButtonActive(boolean isActive);

    AbstractAttachmentUploader getFileUploader();

    void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler);

    HasVisibility addAttachmentUploaderVisibility();

    HasVisibility createSubtaskButtonVisibility();
}
