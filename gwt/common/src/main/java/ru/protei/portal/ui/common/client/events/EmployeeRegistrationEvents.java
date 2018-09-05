package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.EmployeeRegistration;

public class EmployeeRegistrationEvents {

    @Url( value = "employee_registrations", primary = true )
    public static class Show {
        public Show () {}
    }

    @Url( value = "employee_registration")
    public static class Create {
    }

    public static class ShowPreview {

        public ShowPreview (HasWidgets parent, EmployeeRegistration employeeRegistration) {
            this.parent = parent;
            this.employeeRegistration = employeeRegistration;
        }

        public EmployeeRegistration employeeRegistration;
        public HasWidgets parent;
    }
}
