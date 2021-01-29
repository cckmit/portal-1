package ru.protei.portal.ui.common.client.activity.casehistory.list;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface AbstractCaseHistoryListView extends IsWidget {
    void setActivity(AbstractCaseHistoryListActivity activity);

    HasWidgets.ForIsWidget root();
}
