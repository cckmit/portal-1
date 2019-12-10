package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

/**
 * Представление создания и редактирования обращения
 */
public interface AbstractIssueEditView extends IsWidget {

    void setActivity( AbstractIssueEditActivity activity );

    HasWidgets getMetaContainer();

    HasValue<String> name();
    HasValue<String> description();
    HasValue<Boolean> isPrivate();

    HasValidable nameValidator();

    HasVisibility numberVisibility();

    HasWidgets getCommentsContainer();
    HasAttachments attachmentsContainer();

    HasVisibility numberContainerVisibility();

    void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler);
    void setCaseNumber(Long caseNumber);

    void setCreatedBy(String value);

    HasVisibility copyNumberVisibility();

    void showComments(boolean isShow);
    boolean isAttached();

    void setPrivacyIcon(Boolean isPrivate);

    HasVisibility saveVisibility();

    void setNumber(Integer num);

    HasVisibility privacyVisibility();

    HasEnabled saveEnabled();

    void setDescriptionPreviewAllowed( boolean isPreviewAllowed );

    void switchToRONameAndDescriptionView( boolean b);

    void setDescriptionRO(String value);

    void setNameRO(String name, String jiraUrl);

    String DESCRIPTION = "description";

    HasWidgets getTagsContainer();

    HasWidgets getLinksContainer();

    HasVisibility copyNumberAndNameVisibility();

    HasVisibility editNameAndDescriptionButtonVisibility();

    void setNameAndDescriptionButtonsPanelVisibility(boolean visible);
}
