package ru.protei.portal.ui.issue.client.activity.item;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractAddingIssueCommentHelpItemView extends IsWidget {
    void addRootStyle(String style);

    void setHelpText(String text);
}
