package ru.protei.portal.ui.ipreservation.client.activity.reservedip.filter;

import ru.protei.portal.ui.common.client.widget.typedrangepicker.TypedSelectorRangePicker;

/**
 * Активность поиска зарезервированных IP
 */
public interface AbstractReservedIpFilterActivity {
    void onFilterChanged();
    boolean validateTypedSelectorRangePicker(TypedSelectorRangePicker dateIntervalWithType);
}