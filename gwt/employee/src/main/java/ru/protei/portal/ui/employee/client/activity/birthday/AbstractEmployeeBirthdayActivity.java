package ru.protei.portal.ui.employee.client.activity.birthday;

import ru.protei.portal.core.model.struct.EmployeeBirthday;

public interface AbstractEmployeeBirthdayActivity {

    void onBirthdayClicked(EmployeeBirthday birthday);

    void onOneMonthBackClicked();

    void onShowTodayButtonClicked();

    void onOneMonthForwardClicked();

    void onReloadClicked();
}
