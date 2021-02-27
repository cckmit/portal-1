package ru.protei.portal.ui.issue.client.activity.item;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractAddingIssueCommentHelpItemView extends IsWidget {
    void addRootStyle(String style);

    void setHeader(String header);

    void setText(String text);
}
