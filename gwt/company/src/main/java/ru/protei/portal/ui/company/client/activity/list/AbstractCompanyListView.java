package ru.protei.portal.ui.company.client.activity.list;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.ent.CompanyGroup;

import java.util.Set;

/**
 * Created by turik on 27.09.16.
 */
public interface AbstractCompanyListView extends IsWidget {

    void setActivity( AbstractCompanyListActivity activity );
    HasWidgets getChildContainer();
    HasValue< String > getSearchPattern();
    HasValue< CompanyGroup > getGroup();
    HasValue< Set< CompanyCategory > > getCategories();
    HasValue< En_SortField > getSortField();
    HasValue< Boolean > getSortDir();
    void resetFilter();
}
