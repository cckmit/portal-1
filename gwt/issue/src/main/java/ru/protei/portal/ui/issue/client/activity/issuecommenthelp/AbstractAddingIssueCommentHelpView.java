package ru.protei.portal.ui.issue.client.activity.issuecommenthelp;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractAddingIssueCommentHelpView extends IsWidget {
    void setActivity(AbstractAddingIssueCommentHelpActivity activity);

    HasWidgets textContainer();
}
