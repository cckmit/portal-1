package ru.protei.portal.ui.ipreservation.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;

/**
 * Представление поиска зарезервированного IP
 */
public interface AbstractIpReservationFilterView extends IsWidget {
    void setActivity( AbstractIpReservationFilterActivity activity);
    HasValue<String> search();
    HasValue<DateInterval> dateReservedRange();
    HasValue<DateInterval> dateReleasedRange();
    void resetFilter();
}
