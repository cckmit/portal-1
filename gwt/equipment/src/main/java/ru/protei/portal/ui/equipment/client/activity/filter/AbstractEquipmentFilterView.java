package ru.protei.portal.ui.equipment.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;

/**
 * Абстракция вида фильтра оборудования
 */
public interface AbstractEquipmentFilterView extends IsWidget {

    void setActivity( AbstractEquipmentFilterActivity activity );

    HasValue<String> name();

    void resetFilter();
}