package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.function.Consumer;

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

    void setCopyNameAndNumberText(String copyText, Consumer<Boolean> callback);

    void setCopyNameText(String copyText, Consumer<Boolean> callback);

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
