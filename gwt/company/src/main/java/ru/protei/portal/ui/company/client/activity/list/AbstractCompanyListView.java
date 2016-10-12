package ru.protei.portal.ui.company.client.activity.list;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CompanyGroup;

/**
 * Created by turik on 27.09.16.
 */
public interface AbstractCompanyListView extends IsWidget {

    void setActivity( AbstractCompanyListActivity activity );
    HasWidgets getCompanyContainer();
    String getSearchPattern();
    HasValue< CompanyGroup > getCompanyGroup();
    HasValue< En_SortField > getSortField();
    void resetFilter();
}
