package ru.protei.portal.ui.absence.client.activity.report;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.absence.client.widget.paramview.AbsenceFilterParamWidget;

public interface AbstractAbsenceReportCreateView extends IsWidget {
    HasValue<String> name();
    AbsenceFilterParamWidget getFilterParams();
    void resetFilter();
}
