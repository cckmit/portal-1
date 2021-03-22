package ru.protei.portal.ui.issue.client.activity.help;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractIssueCommentHelpView extends IsWidget {
    void setActivity(AbstractIssueCommentHelpActivity activity);

    DivElement helpTextContainer();
}
