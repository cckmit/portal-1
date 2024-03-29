package ru.protei.portal.ui.employeeregistration.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CaseState;

import java.util.Set;

public interface AbstractEmployeeRegistrationFilterView extends IsWidget {

    void setActivity(AbstractEmployeeRegistrationFilterActivity activity);

    void resetFilter();

    HasValue<String> searchString();

    HasValue<DateInterval> dateRange();

    HasValue<Set<CaseState>> states();

    HasValue<En_SortField> sortField();

    HasValue<Boolean> sortDir();
}
