package ru.protei.portal.ui.product.client.activity.list;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;

public interface AbstractProductListView extends IsWidget {

    void setActivity( AbstractProductListActivity activity );

    HasWidgets getItemsContainer ();

    HasText getSearchPattern();

    HasValue<Boolean> isShowDepricated();

    HasValue<En_SortField> getSortField ();

    HasValue<Boolean> getSortDir();

    void reset();
}
