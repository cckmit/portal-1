package ru.protei.portal.ui.issuereport.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Report;

public interface AbstractIssueReportTableView extends IsWidget {

    void setActivity(AbstractIssueReportTableActivity activity);

    void clearRecords();

    void triggerTableLoad();

    void setTotalRecords(int totalRecords);

    int getPageCount();

    void scrollTo(int page);

    void updateRow(Report item);

    HasWidgets getPagerContainer();
}
