package ru.protei.portal.ui.product.client.activity.list;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;

public interface AbstractProductListView extends IsWidget {

    void setActivity( AbstractProductListActivity activity );

    HasWidgets getItemsContainer();

    HasValue<String> searchPattern();

    HasValue<Boolean> showDeprecated();

    HasValue<En_SortField> sortField();

    HasValue<Boolean> sortDir();
}
