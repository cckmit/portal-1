package ru.protei.portal.ui.role.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;

/**
 * Абстракция вида фильтра роли
 */
public interface AbstractRoleFilterView extends IsWidget {

    void setActivity( AbstractRoleFilterActivity activity );

    HasValue<En_SortField> sortField();

    HasValue<Boolean> sortDir();

    HasValue< String > searchPattern();

    void resetFilter();
}