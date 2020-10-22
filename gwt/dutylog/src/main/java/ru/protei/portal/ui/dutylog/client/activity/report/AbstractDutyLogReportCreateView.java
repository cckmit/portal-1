package ru.protei.portal.ui.dutylog.client.activity.report;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.dutylog.client.widget.filter.paramview.DutyLogFilterParamWidget;

public interface AbstractDutyLogReportCreateView extends IsWidget {
    HasValue<String> name();
    DutyLogFilterParamWidget getFilterParams();
    void resetFilter();
}