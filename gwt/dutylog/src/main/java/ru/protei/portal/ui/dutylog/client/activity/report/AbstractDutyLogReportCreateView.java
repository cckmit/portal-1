package ru.protei.portal.ui.dutylog.client.activity.report;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.dutylog.client.widget.filter.DutyLogFilterWidget;

public interface AbstractDutyLogReportCreateView extends IsWidget {
    HasValue<String> name();
    DutyLogFilterWidget getFilterWidget();
}