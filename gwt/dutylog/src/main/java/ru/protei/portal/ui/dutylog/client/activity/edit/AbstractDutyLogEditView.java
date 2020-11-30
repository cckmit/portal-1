package ru.protei.portal.ui.dutylog.client.activity.edit;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_DutyType;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

public interface AbstractDutyLogEditView extends IsWidget {

    void setActivity(AbstractDutyLogEditActivity activity);
    HasValue<PersonShortView> employee();
    HasValue<DateInterval> dateRange();
    HasValue<En_DutyType> type();
    HasVisibility contentVisibility();
    HasVisibility loadingVisibility();
    HasValidable employeeValidator();
}
