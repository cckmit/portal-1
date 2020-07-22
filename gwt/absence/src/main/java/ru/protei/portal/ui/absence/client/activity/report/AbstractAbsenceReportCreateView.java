package ru.protei.portal.ui.absence.client.activity.report;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.absence.client.view.report.paramview.AbsenceFilterParamView;

public interface AbstractAbsenceReportCreateView extends IsWidget {
    HasValue<String> name();
    AbsenceFilterParamView getFilterParams();
    void resetFilter();
}
