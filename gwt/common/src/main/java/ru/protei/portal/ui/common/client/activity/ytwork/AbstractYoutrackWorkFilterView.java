package ru.protei.portal.ui.common.client.activity.ytwork;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;

public interface AbstractYoutrackWorkFilterView extends IsWidget {

    void setActivity(AbstractYoutrackWorkFilterActivity activity);

    void resetFilter(boolean withRefreshTable);

    void clearFooterStyle();

    HasValue<DateIntervalWithType> date();

    void setDateValid(boolean isTypeValid, boolean isRangeValid);
}
