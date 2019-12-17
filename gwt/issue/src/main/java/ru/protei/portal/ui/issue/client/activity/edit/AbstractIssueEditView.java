package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Представление создания и редактирования обращения
 */
public interface AbstractIssueEditView extends IsWidget, AbstractIssueView {
    void setActivity( AbstractIssueEditActivity activity );

    HasVisibility editNameAndDescriptionButtonVisibility();

    boolean isAttached();

    String DESCRIPTION = "description";
}
