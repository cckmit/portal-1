package ru.protei.portal.ui.employee.client.activity.item;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractInfoItemView extends IsWidget {
    void setActivity(AbstractInfoItemActivity activity);

    void setName(String name, String link);

    void setBirthday(String birthday);

    void setPhones(String phones);

    void setEmail(String email);

    HasVisibility birthdayContainerVisibility();

    HasVisibility phonesContainerVisibility();

    HasVisibility emailContainerVisibility();
}
