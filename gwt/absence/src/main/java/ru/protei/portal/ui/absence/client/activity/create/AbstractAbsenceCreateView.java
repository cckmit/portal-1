package ru.protei.portal.ui.absence.client.activity.create;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.ui.absence.client.activity.common.AbstractAbsenceCommonActivity;

import java.util.List;

public interface AbstractAbsenceCreateView extends IsWidget {
    void setActivity(AbstractAbsenceCommonActivity activity);
    HasValue<List<DateInterval>> dateRange();
    HasEnabled dateRangeEnabled();
    void setDateRangeValid(boolean isValid);
}
