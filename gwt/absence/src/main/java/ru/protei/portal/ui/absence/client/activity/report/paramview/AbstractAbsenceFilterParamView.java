package ru.protei.portal.ui.absence.client.activity.report.paramview;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.ui.absence.client.activity.report.AbstractAbsenceReportCreateActivity;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterParamView;

public interface AbstractAbsenceFilterParamView extends IsWidget, FilterParamView<AbsenceQuery> {
    void setActivity(AbstractAbsenceReportCreateActivity activity);
    boolean isValidDateRange();
    void setDateRangeValid(boolean isValid);
}
