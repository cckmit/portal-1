package ru.protei.portal.ui.delivery.client.activity.pcborder.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;

public interface AbstractPcbOrderFilterView extends IsWidget {
    void setActivity(AbstractPcbOrderFilterActivity activity);

    HasValue<String> search();

    HasValue<En_SortField> sortField();
    HasValue<Boolean> sortDir();

    void resetFilter();
}
