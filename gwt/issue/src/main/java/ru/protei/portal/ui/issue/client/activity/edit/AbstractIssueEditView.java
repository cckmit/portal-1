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

    HasValidable nameValidator();

    HasWidgets getCommentsContainer();
    HasAttachments attachmentsContainer();

    void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler);
    void setCaseNumber(Long caseNumber);

    void setCreatedBy(String value);

    boolean isAttached();

    void setPrivacyIcon(Boolean isPrivate);

    void setNumber(Integer num);

    void setDescriptionPreviewAllowed( boolean isPreviewAllowed );

    void switchToRONameAndDescriptionView( boolean b);

    void setDescriptionRO(String value);

    void setNameRO(String name, String jiraUrl);

    String DESCRIPTION = "description";

    HasWidgets getTagsContainer();

    HasWidgets getLinksContainer();

    HasVisibility editNameAndDescriptionButtonVisibility();
}
