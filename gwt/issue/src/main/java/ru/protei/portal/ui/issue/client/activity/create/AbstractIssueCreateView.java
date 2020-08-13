package ru.protei.portal.ui.issue.client.activity.create;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;


public interface AbstractIssueCreateView extends IsWidget {
    void setActivity(AbstractIssueCreateActivity issueCreateActivity);

    HasValue<String> name();

    HasValue<String> description();

    HasValue<Boolean> isPrivate();

    HasValidable nameValidator();

    HasAttachments attachmentsListContainer();

    void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler);

    HasVisibility saveVisibility();

    HasVisibility privacyVisibility();

    HasEnabled saveEnabled();

    HasWidgets getTagsContainer();

    HasWidgets getLinksContainer();

    void setDescriptionPreviewAllowed(boolean isPreviewAllowed );

    String DESCRIPTION = "description";

    HasWidgets getIssueMetaViewContainer();

    boolean isFavoriteButtonActive();

    void setFavoriteButtonActive(boolean isActive);

    void setCountOfAttachments(int countOfAttachments);

    HasVisibility attachmentsRootContainerVisibility();

    void setAttachmentContainerShow(boolean isShow);
}
