package ru.protei.portal.ui.plan.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

public interface AbstractPlanEditView extends IsWidget {
    void setActivity(AbstractPlanEditActivity activity);

    HasValue<String> name();

    HasValidable nameValidator();

    HasValue<DateInterval> planPeriod();

    HasWidgets unassignedTableContainer();

    HasWidgets assignedTableContainer();

    HasVisibility editButtonVisibility();

    HasVisibility saveButtonVisibility();

    HasVisibility cancelButtonVisibility();

    HasVisibility backButtonVisibility();

    HasEnabled nameEnabled();

    HasEnabled periodEnabled();
}
