package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;

public class AbsenceEvents {

    @Url(value = "summarytable")
    public static class ShowSummaryTable {
        public ShowSummaryTable() {
        }
    }

    public static class Update {
        public Update() {
        }
    }

    public static class Show {

        public HasWidgets parent;
        public Long employeeId;

        public Show(HasWidgets parent, Long employeeId) {
            this.parent = parent;
            this.employeeId = employeeId;
        }
    }

    public static class Edit {

        public Long id;

        public Edit() { this.id = null; }
        public Edit (Long id) {
            this.id = id;
        }
    }

    public static class CreateReport {}
}
