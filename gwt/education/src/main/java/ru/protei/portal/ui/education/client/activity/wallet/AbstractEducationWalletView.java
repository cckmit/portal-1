package ru.protei.portal.ui.education.client.activity.wallet;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractEducationWalletView extends IsWidget {

    void setActivity(AbstractEducationWalletActivity activity);

    void setDepartmentName(String departmentName);

    void setCoins(Integer coins);

    void setCountConference(Long count);

    void setCountCourse(Long count);

    void setCountLiterature(Long count);
}
