package ru.protei.portal.ui.dutylog.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.DutyType;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.enterprise1c.Response1C;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;

import java.util.Set;

public interface AbstractDutyLogFilterView extends IsWidget {

    void setActivity(AbstractDutyLogFilterActivity activity);

    void resetFilter();

    HasValue<En_SortField> sortField();

    HasValue<Boolean> sortDir();

    HasValue<Set<PersonShortView>> employees();

    HasValue<DateIntervalWithType> date();

    HasValue<Set<DutyType>> type();
}
