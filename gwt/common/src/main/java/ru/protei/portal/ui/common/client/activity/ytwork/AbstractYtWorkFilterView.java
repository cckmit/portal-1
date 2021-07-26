package ru.protei.portal.ui.common.client.activity.ytwork;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;

public interface AbstractYtWorkFilterView extends IsWidget {

    void setActivity(AbstractYtWorkFilterActivity activity);

    void resetFilter();

    void clearFooterStyle();

    HasValue<DateIntervalWithType> date();
}
