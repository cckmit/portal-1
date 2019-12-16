package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

/**
 * Представление создания и редактирования обращения
 */
public interface AbstractIssueEditView extends IsWidget, AbstractIssueDetailsView {

    void setActivity( AbstractIssueEditActivity activity );

//    HasValue<String> name();
//    HasValue<String> description();
//    HasValidable nameValidator();

    void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler);
//    void setCaseNumber(Long caseNumber);
//
//    void setCreatedBy(String value);

    boolean isAttached();

//    void setPrivacyIcon(Boolean isPrivate);

//    void setNumber(Integer num);

//    void setDescriptionPreviewAllowed( boolean isPreviewAllowed );

//    void switchToRONameAndDescriptionView( boolean b);

//    void setDescriptionRO(String value);
//
//    void setNameRO(String name, String jiraUrl);

    String DESCRIPTION = "description";

//    HasVisibility copyNumberAndNameVisibility();

//    HasVisibility editNameAndDescriptionButtonVisibility();

//    void setNameAndDescriptionButtonsPanelVisibility(boolean visible);
}
