package ru.protei.portal.ui.company.client.activity.list;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.Set;

/**
 * Представление списка компаний
 */
public interface AbstractCompanyListView extends IsWidget {

    void setActivity( AbstractCompanyListActivity activity );
    HasWidgets getChildContainer();
    HasValue< String > searchPattern();
    HasValue< EntityOption > group();
    HasValue< Set< CompanyCategory > > categories();
    HasValue< En_SortField > sortField();
    HasValue< Boolean > sortDir();
    void resetFilter();
}
