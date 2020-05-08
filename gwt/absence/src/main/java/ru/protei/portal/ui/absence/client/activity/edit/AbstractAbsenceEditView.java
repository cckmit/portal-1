package ru.protei.portal.ui.absence.client.activity.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.view.PersonShortView;

public interface AbstractAbsenceEditView extends IsWidget {

    void setActivity(AbstractAbsenceEditActivity activity);
    HasValue<PersonShortView> employee();
    HasValue<DateInterval> dateRange();
    HasValue<En_AbsenceReason> reason();
    HasValue<String> comment();
    HasVisibility contentVisibility();
    HasEnabled employeeEnabled();
    HasEnabled dateRangeEnabled();
    HasEnabled reasonEnabled();
    HasEnabled commentEnabled();
}
