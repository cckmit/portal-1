package ru.protei.portal.ui.ipreservation.client.activity.subnet.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;

/**
 * Представление поиска подсетей
 */
public interface AbstractSubnetFilterView extends IsWidget {
    void setActivity(AbstractSubnetFilterActivity activity);

    HasValue<String> search();

    HasValue<En_SortField> sortField();
    HasValue< Boolean > sortDir();

    void resetFilter();
}