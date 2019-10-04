package ru.protei.portal.ui.issue.client.activity.preview;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.timefield.HasTime;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;

import java.util.Set;

/**
 * Абстракция вида превью обращения
 */
public interface AbstractIssuePreviewView extends IsWidget {

    void setActivity(AbstractIssuePreviewActivity activity);

    void setPrivateIssue(boolean privateIssue);
    void setCaseNumber(Long caseNumber);
    void setCreatedBy(String value);
    void setState(long value );
    void setImportance(int value);
    void setProduct(String value);

    void setLinks(Set<CaseLink> value);
    void setTags(Set<CaseTag> value);

    void setContact(String value);
    void setManager(String value);
    void setName(String value);

    String getName();

    void setInfo(String value);

    void setPlatformName(String value);
    void setPlatformLink(String link);
    HasVisibility platformVisibility();

    void setSubscriptionEmails(String value);
    HasTime timeElapsed();
    HasVisibility timeElapsedContainerVisibility();

    HasVisibility jiraContainerVisibility();

    void setJiraIssueType(String value);
    void setJiraSeverity(String value);
    void setJiraTimeOfReaction(String value);
    void setJiraTimeOfDecision(String value);

    HasWidgets getCommentsContainer();
    HasAttachments attachmentsContainer();

    void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler);
    boolean isAttached();

    HasVisibility backBtnVisibility();
}
