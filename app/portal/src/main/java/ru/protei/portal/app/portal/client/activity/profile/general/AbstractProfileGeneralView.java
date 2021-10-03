package ru.protei.portal.app.portal.client.activity.profile.general;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractProfileGeneralView extends IsWidget {

    void setActivity(AbstractProfileGeneralActivity activity);

    void setLogin(String login);

    HasVisibility changePasswordButtonVisibility();

    HasVisibility newEmployeeBookContainerVisibility();
}
