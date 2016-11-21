package ru.protei.portal.ui.contact.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;

/**
 * Абстракция вида фильтра контактов
 */
public interface AbstractContactFilterView extends IsWidget {

    void setActivity( AbstractContactFilterActivity activity );

    HasValue<EntityOption> company();
    HasValue<Boolean> showFired();
    HasValue<En_SortField> sortField();
    HasValue< Boolean > sortDir();
    HasValue< String > searchPattern();
    void resetFilter();
}