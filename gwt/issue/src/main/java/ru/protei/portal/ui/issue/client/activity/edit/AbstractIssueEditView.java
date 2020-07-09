package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

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
}
