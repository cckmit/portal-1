package ru.protei.portal.app.portal.client.activity.profile;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида профиля
 */
public interface AbstractProfilePageView extends IsWidget {

    void setActivity( AbstractProfilePageActivity activity );

    void setName(String name);

    void setCompany(String value);

    void setIcon(String iconSrc);

    HasWidgets getGeneralContainer();

    HasWidgets getSubscriptionsContainer();

    void resetTabs();
}
