package ru.protei.portal.ui.delivery.client.activity.card.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.ui.delivery.client.activity.card.table.AbstractCardTableActivity;
import ru.protei.portal.ui.delivery.client.widget.card.CardStatesOptionList;

public interface AbstractCardFilterView extends IsWidget {
    void setActivity(AbstractCardTableActivity cardTableActivity);

    HasValue<String> search();

    HasValue<En_SortField> sortField();
    HasValue<Boolean> sortDir();
    HasValue<Long> type();
    CardStatesOptionList states();
    HasValue<Long> manager();

    void resetFilter();
}
