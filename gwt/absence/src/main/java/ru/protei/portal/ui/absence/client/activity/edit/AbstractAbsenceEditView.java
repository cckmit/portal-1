package ru.protei.portal.ui.absence.client.activity.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

public interface AbstractAbsenceEditView extends IsWidget {

    void setActivity(AbstractAbsenceEditActivity activity);
    HasValue<PersonShortView> employee();
    HasValue<DateInterval> dateRange();
    HasValue<En_AbsenceReason> reason();
    HasValue<String> comment();
    HasVisibility contentVisibility();
    HasVisibility loadingVisibility();
    HasEnabled employeeEnabled();
    HasEnabled dateRangeEnabled();
    HasEnabled reasonEnabled();
    HasEnabled commentEnabled();
    HasValidable employeeValidator();
    HasValidable reasonValidator();
    void setDateRangeValid(boolean isValid);
}