package ru.protei.portal.ui.absence.client.activity.edit;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.ui.absence.client.activity.common.AbstractAbsenceCommonActivity;

public interface AbstractAbsenceEditView extends IsWidget {
    void setActivity(AbstractAbsenceCommonActivity activity);
    HasValue<DateInterval> dateRange();
    void setDateRangeValid(boolean isValid);
}
