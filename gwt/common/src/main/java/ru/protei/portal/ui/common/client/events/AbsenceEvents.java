package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
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

    public static class Update {

        public PersonAbsence absence;

        public Update(PersonAbsence absence) {
            this.absence = absence;
        }
    }
}
