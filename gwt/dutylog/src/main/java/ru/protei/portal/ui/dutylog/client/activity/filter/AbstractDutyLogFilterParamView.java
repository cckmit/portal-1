package ru.protei.portal.ui.dutylog.client.activity.filter;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.query.DutyLogQuery;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterParamView;

public interface AbstractDutyLogFilterParamView extends IsWidget, FilterParamView<DutyLogQuery> {
    boolean isValidDateRange();
}
