package ru.protei.portal.ui.absence.client.activity.report.paramview;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterParamView;

public interface AbstractAbsenceFilterParamWidget extends IsWidget, FilterParamView<AbsenceQuery> {
    boolean isValidDateRange();
}
