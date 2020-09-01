package ru.protei.portal.ui.employee.client.activity.birthday;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.struct.EmployeesBirthdays;

public interface AbstractEmployeeBirthdayView extends IsWidget {

    void setActivity(AbstractEmployeeBirthdayActivity activity);

    HasValue<EmployeesBirthdays> birthdays();

    TakesValue<String> yearAndMonth();

    HasVisibility loadingVisibility();

    HasVisibility calendarContainerVisibility();
}
