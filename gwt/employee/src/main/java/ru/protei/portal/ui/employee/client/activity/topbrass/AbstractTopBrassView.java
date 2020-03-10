package ru.protei.portal.ui.employee.client.activity.topbrass;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractTopBrassView extends IsWidget {
    void setActivity(AbstractTopBrassActivity activity);

    HasWidgets topContainer();

    HasWidgets bottomContainer();
}
