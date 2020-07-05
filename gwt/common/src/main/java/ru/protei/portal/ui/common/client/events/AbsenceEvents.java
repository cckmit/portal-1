package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.PersonAbsence;

public class AbsenceEvents {

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

    @Url(value = "absence_report", primary = true)
    public static class CreateReport {
        public CreateReport () {}
    }
}
