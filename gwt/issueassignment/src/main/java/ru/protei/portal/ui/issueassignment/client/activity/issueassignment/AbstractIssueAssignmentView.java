package ru.protei.portal.ui.issueassignment.client.activity.issueassignment;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;

public interface AbstractIssueAssignmentView extends IsWidget {

    void setActivity(AbstractIssueAssignmentActivity activity);

    UIObject table();

    UIObject desk();

    HasWidgets tableContainer();

    HasWidgets deskContainer();

    HasWidgets quickview();

    void showQuickview(boolean isShow);
}
