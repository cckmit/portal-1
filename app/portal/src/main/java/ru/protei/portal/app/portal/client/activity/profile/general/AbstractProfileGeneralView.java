package ru.protei.portal.app.portal.client.activity.profile.general;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractProfileGeneralView extends IsWidget {

    void setActivity(AbstractProfileGeneralActivity activity);

    HasValue<String> currentPassword();

    HasValue<String> newPassword();

    HasValue<String> confirmPassword();

    HasVisibility passwordContainerVisibility();

    HasVisibility changePasswordButtonVisibility();
}
