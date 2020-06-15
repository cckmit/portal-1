package ru.protei.portal.ui.plan.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.PersonShortView;

public interface AbstractPlanFilterView extends IsWidget {
    void setActivity(AbstractPlanFilterActivity activity);

    HasValue<En_SortField> sortField();

    HasValue<Boolean> sortDir();

    HasValue<String> search();

    HasValue<PersonShortView> creator();

    HasValue<DateInterval> startRange();

    HasValue<DateInterval> finishRange();

    void resetFilter();
}
