package ru.protei.portal.ui.absence.client.activity.create;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.view.PersonShortView;

public interface AbstractAbsenceCreateView extends IsWidget {

    void setActivity(AbstractAbsenceCreateActivity activity);
    HasValue<PersonShortView> employee();
    HasValue<DateInterval> dateRange();
    HasValue<En_AbsenceReason> reason();
    HasValue<String> comment();
}
