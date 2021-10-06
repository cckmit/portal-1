package ru.protei.portal.ui.delivery.client.activity.cardbatch.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;

import java.util.Set;

public interface AbstractCardBatchFilterView extends IsWidget {
    void setActivity(AbstractCardBatchFilterActivity cardTableActivity);

    HasValue<String> search();

    HasValue<Set<PersonShortView>> contractors();
    HasValue<Set<EntityOption>> cardTypes();
    HasValue<DateIntervalWithType> deadline();
    HasValue<En_SortField> sortField();
    HasValue<Boolean> sortDir();
    HasValue<Set<ImportanceLevel>> importance();
    HasValue<Set<CaseState>> states();

    void resetFilter();
}
