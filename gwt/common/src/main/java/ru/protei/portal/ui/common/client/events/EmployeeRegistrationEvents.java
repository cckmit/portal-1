package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.EmployeeRegistrationShortView;

public class EmployeeRegistrationEvents {

    @Url( value = "employee_registrations", primary = true )
    public static class Show {
        public Show () {}
    }

    @Url( value = "employee_registration")
    public static class Create {
    }

    public static class Edit {
        public Edit(Long employeeRegistrationId) {
            this.employeeRegistrationId = employeeRegistrationId;
        }

        public final Long employeeRegistrationId;
    }

    @Url(value = "employee_registration_preview", primary = true)
    public static class ShowFullScreen {
        public ShowFullScreen() {}

        public ShowFullScreen(Long id )
        {
            this.id = id;
        }

        public Long id;
    }

    public static class ShowPreview {

        public ShowPreview (HasWidgets parent, Long id) {
            this.parent = parent;
            this.id = id;
        }

        public Long id;
        public HasWidgets parent;
    }
}
