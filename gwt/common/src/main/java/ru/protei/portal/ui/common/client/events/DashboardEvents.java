package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.UserDashboard;

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

    public static class EditIssueTable {
        public EditIssueTable() {}
        public EditIssueTable(UserDashboard dashboard) {
            this.dashboard = dashboard;
        }
        public UserDashboard dashboard;
    }

    public static class EditProjectTable {
        public EditProjectTable() {}
        public EditProjectTable(UserDashboard dashboard) {
            this.dashboard = dashboard;
        }
        public UserDashboard dashboard;
    }



    public static class ChangeTableModel {
        public ChangeTableModel() {}
    }
}
