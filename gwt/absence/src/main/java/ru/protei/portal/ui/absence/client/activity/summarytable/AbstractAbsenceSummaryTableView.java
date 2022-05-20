package ru.protei.portal.ui.absence.client.activity.summarytable;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.ui.absence.client.widget.filter.AbsenceFilterWidget;

public interface AbstractAbsenceSummaryTableView extends IsWidget {
    void setActivity(AbstractAbsenceSummaryTableActivity activity);
    AbsenceFilterWidget getFilterWidget();
    void clearRecords();
    HasWidgets getPagerContainer();
    void setTotalRecords(int totalRecords);
    int getPageCount();
    void scrollTo(int page);
    void triggerTableLoad();
}