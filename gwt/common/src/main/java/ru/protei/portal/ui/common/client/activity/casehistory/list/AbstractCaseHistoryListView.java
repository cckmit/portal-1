package ru.protei.portal.ui.common.client.activity.casehistory.list;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractCaseHistoryListView extends IsWidget {
    void setActivity(AbstractCaseHistoryListActivity activity);

    HasWidgets.ForIsWidget root();
}
