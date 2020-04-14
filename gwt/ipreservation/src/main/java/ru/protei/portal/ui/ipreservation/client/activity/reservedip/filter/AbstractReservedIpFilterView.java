package ru.protei.portal.ui.ipreservation.client.activity.reservedip.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.SubnetOption;

import java.util.Set;

/**
 * Представление поиска зарезервированных IP
 */
public interface AbstractReservedIpFilterView extends IsWidget {
    void setActivity( AbstractReservedIpFilterActivity activity);

    HasValue<String> search();
    HasValue<Set<SubnetOption>> subnets();
    HasValue<PersonShortView> owner();
    HasValue<DateInterval> reserveRange();
    HasValue<DateInterval> releaseRange();
    HasValue<DateInterval> lastActiveRange();

    HasValue<En_SortField> sortField();
    HasValue< Boolean > sortDir();

    void resetFilter();
}