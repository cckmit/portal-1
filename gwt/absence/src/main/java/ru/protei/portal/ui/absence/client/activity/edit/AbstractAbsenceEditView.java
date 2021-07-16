package ru.protei.portal.ui.absence.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.dto.ScheduleItem;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.List;

public interface AbstractAbsenceEditView extends IsWidget {

    void setActivity(AbstractAbsenceEditActivity activity);
    HasValue<PersonShortView> employee();
    HasValue<En_AbsenceReason> reason();
    HasValue<String> comment();
    HasVisibility contentVisibility();
    HasVisibility loadingVisibility();
    HasEnabled employeeEnabled();
    HasEnabled reasonEnabled();
    HasValidable employeeValidator();
    HasValidable reasonValidator();
    HasValue<DateInterval> dateRange();
    HasVisibility scheduleVisibility();

    HasVisibility scheduleCreateVisibility();

    HasValue<Boolean> enableSchedule();

    void setDateRangeValid(boolean isValid);

    HasEnabled enableScheduleEnabled();

    HasValue<List<ScheduleItem>> scheduleItems();
}
