package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.brainworm.factory.context.client.annotation.Url;

public class AbsenceEvents {

    @Url(value = "absences", primary = true)
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

    public static class Create {
        public EmployeeShortView employee;
        public Create withEmployee(EmployeeShortView employee) {
            this.employee = employee;
            return this;
        }
    }

    public static class CreateReport {}
}
