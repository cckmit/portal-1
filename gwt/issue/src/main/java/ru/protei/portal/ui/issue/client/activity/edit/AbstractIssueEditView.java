package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.issue.client.activity.meta.AbstractIssueMetaActivity;
import ru.protei.portal.ui.issue.client.activity.meta.AbstractIssueMetaView;

import java.util.Set;

/**
 * Представление создания и редактирования обращения
 */
public interface AbstractIssueEditView extends IsWidget {

    void setActivity( AbstractIssueEditActivity activity );
    void setMetaActivity( AbstractIssueMetaActivity activity );

    AbstractIssueMetaView getMetaView();

    HasValue<String> name();
    HasValue<String> description();
    HasValue<Set<CaseLink>> links();
    HasValue<Set<CaseTag>> tags();

    HasValidable nameValidator();

    HasWidgets getCommentsContainer();
    HasAttachments attachmentsContainer();

    void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler);
    void setCaseNumber(Long caseNumber);

    void setCreatedBy(String value);

    boolean isAttached();

    void setPrivacyIcon(Boolean isPrivate);

    HasVisibility saveVisibility();


    void setNumber(Integer num);

    HasEnabled saveEnabled();

    void setTagsAddButtonEnabled(boolean enabled);

    void setTagsEditButtonEnabled(boolean enabled);

    void setDescriptionPreviewAllowed( boolean isPreviewAllowed );

    void switchToRONameDescriptionView(boolean b);

    void setDescriptionRO(String value);

    void setNameRO(String name, String jiraUrl);

    String DESCRIPTION = "description";
}
