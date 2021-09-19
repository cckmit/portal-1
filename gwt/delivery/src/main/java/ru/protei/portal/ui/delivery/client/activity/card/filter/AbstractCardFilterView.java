package ru.protei.portal.ui.delivery.client.activity.card.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.view.CardTypeOption;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Set;

public interface AbstractCardFilterView extends IsWidget {
    void setActivity(AbstractCardFilterActivity cardTableActivity);

    HasValue<String> search();

    HasValue<En_SortField> sortField();
    HasValue<Boolean> sortDir();
    HasValue<Set<CardTypeOption>> types();
    HasValue<Set<CaseState>> states();
    HasValue<Set<PersonShortView>> managers();

    void resetFilter();
}
