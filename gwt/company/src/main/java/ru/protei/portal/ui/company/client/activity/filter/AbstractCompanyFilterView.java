package ru.protei.portal.ui.company.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.Set;

/**
 * Абстракция вида фильтра компаний
 */
public interface AbstractCompanyFilterView extends IsWidget {

    void setActivity( AbstractCompanyFilterActivity activity );
    HasValue< String > searchPattern();

    HasValue<Boolean> showDeprecated();

    HasValue<Set< EntityOption >> categories();
    HasValue< En_SortField > sortField();
    HasValue< Boolean > sortDir();
    void resetFilter();
}