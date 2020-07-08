package ru.protei.portal.ui.absence.client.activity.report;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Set;

public interface AbstractAbsenceReportCreateView extends IsWidget {
    void setActivity(AbstractAbsenceReportCreateActivity activity);
    HasValue<String> name();
    HasValue<DateInterval> dateRange();
    HasValue<Set<PersonShortView>> employees();
    HasValue<Set<En_AbsenceReason>> reasons();
    HasValue<En_SortField> sortField();
    HasValue<Boolean> sortDir();
}
