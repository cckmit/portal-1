package ru.protei.portal.ui.issuereport.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

public interface AbstractIssueReportTableView extends IsWidget {

    void setActivity(AbstractIssueReportTableActivity activity);
    void setAnimation(TableAnimation animation);
    void clearRecords();

    HasWidgets getPreviewContainer();
    HasWidgets getFilterContainer();

    void setReportsCount(Long issuesCount);

    int getPageSize();

    int getPageCount();

    void scrollTo(int page);

    void updateRow(Report item);

    void hideElements();
    void showElements();
}
