package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;

public class IssueAssignmentEvents {

    @Url(value = "issue_assignment", primary = true)
    public static class Show {
        public Show () {}
    }

    public static class ShowTable {
        public HasWidgets parent;
        public ShowTable () {}
        public ShowTable (HasWidgets parent) {
            this.parent = parent;
        }
    }

    public static class ShowDesk {
        public HasWidgets parent;
        public ShowDesk () {}
        public ShowDesk (HasWidgets parent) {
            this.parent = parent;
        }
    }
}
