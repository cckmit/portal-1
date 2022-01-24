package ru.protei.portal.ui.employee.client.activity.preview;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Представление превью сотрудника
 */
public interface AbstractEmployeePreviewView extends IsWidget {

    void setActivity(AbstractEmployeePreviewActivity activity);

    void setPhotoUrl(String url);

    void setName(String name);

    void setBirthday(String birthday);

    void setPhones(String phones);

    void setEmail(String email);

    void setID(String value);

    void setIP(String ip);
    
    void setInn(String inn);

    void setLogins(String logins);

    void setRestVacationDays(String restVacationDays);

    HasVisibility birthdayContainerVisibility();

    HasVisibility phonesContainerVisibility();

    HasVisibility emailContainerVisibility();

    HasWidgets positionsContainer();

    HasWidgets absencesContainer();

    void showFullScreen(boolean isFullScreen);

    void showAbsencesPanel(boolean isShow);

    Element getRestVacationDaysLoading();
}
