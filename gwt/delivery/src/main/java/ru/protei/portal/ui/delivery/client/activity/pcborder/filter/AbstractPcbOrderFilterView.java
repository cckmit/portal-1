package ru.protei.portal.ui.delivery.client.activity.pcborder.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_PcbOrderPromptness;
import ru.protei.portal.core.model.dict.En_PcbOrderState;
import ru.protei.portal.core.model.dict.En_PcbOrderType;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.Set;

public interface AbstractPcbOrderFilterView extends IsWidget {
    void setActivity(AbstractPcbOrderFilterActivity activity);

    HasValue<En_SortField> sortField();

    HasValue<Boolean> sortDir();

    HasValue<Set<EntityOption>> types();

    HasValue<Set<En_PcbOrderType>> orderType();

    HasValue<Set<En_PcbOrderState>> states();

    HasValue<Set<En_PcbOrderPromptness>> promptness();

    void resetFilter();
}
