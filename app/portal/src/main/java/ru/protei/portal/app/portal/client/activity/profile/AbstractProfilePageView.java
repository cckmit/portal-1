package ru.protei.portal.app.portal.client.activity.profile;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида превью контакта
 */
public interface AbstractProfilePageView extends IsWidget {

    void setActivity( AbstractProfilePageActivity activity );

    void setName( String name );

    void setCompany( String value );

    HasValue<String> currentPassword();

    HasValue<String> newPassword();

    HasValue<String> confirmPassword();

    HasVisibility passwordContainerVisibility();

    HasVisibility changePasswordButtonVisibility();

    void setIcon(String iconSrc);

    void setPersonId(Long personId);

    HasVisibility personCaseFilterContainerVisibility();
}
