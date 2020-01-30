package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.shared.model.Profile;

/**
 * События по дашборду
 */
public class DashboardEvents {

    /**
     * used in {@link ru.protei.portal.ui.common.client.common.UiConstants}
     */
    @Url( value = "dashboard", primary = true )
    public static class Show {
        public Show () {}
    }
}
