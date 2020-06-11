package ru.protei.portal.ui.plan.client.activity.edit;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

public interface AbstractPlanEditView extends IsWidget {
    void setActivity(AbstractPlanEditActivity activity);

    HasValue<String> name();

    HasValidable nameValidator();

    HasValue<DateInterval> planPeriod();

    HasWidgets unassignedTableContainer();

    HasWidgets assignedTableContainer();
}
