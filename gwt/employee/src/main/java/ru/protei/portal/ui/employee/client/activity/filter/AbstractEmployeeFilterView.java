package ru.protei.portal.ui.employee.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.Set;

/**
 * Представление фильтра сотрудников
 */
public interface AbstractEmployeeFilterView extends IsWidget {
    void setActivity( AbstractEmployeeFilterActivity activity );
    HasValue< En_SortField > sortField();
    HasValue< Boolean > sortDir();
    HasValue< String > searchPattern();
    HasValue< String > workPhone();
    HasValue< String > mobilePhone();
    HasValue< String > ipAddress();
    void resetFilter();
}
