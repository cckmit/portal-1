package ru.protei.portal.ui.issueassignment.client.activity.issueassignment;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractIssueAssignmentView extends IsWidget {

    void setActivity(AbstractIssueAssignmentActivity activity);

    HasWidgets tableContainer();

    HasWidgets deskContainer();
}
