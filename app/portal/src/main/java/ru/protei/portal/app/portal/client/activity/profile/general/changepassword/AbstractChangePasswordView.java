package ru.protei.portal.app.portal.client.activity.profile.general.changepassword;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractChangePasswordView extends IsWidget {

    HasValue<String> currentPassword();

    HasValue<String> newPassword();

    HasValue<String> confirmPassword();
}
