package ru.protei.portal.ui.common.client.activity.transportationrequestfilter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;


public interface AbstractTransportationRequestFilterView extends IsWidget {

    void setActivity(AbstractTransportationRequestFilterActivity activity);

    void resetFilter();

    void clearFooterStyle();

    HasValue<DateIntervalWithType> pickupDate();

    void setDateValid(boolean isTypeValid, boolean isRangeValid);
}
