package ru.protei.portal.ui.role.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида фильтра роли
 */
public interface AbstractRoleFilterView extends IsWidget {

    void setActivity( AbstractRoleFilterActivity activity );

    HasValue< String > searchPattern();

    void resetFilter();
}