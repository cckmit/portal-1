package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;

public class EmployeeRegistrationEvents {

    @Url( value = "employee_registrations", primary = true )
    public static class Show {
        @Omit
        public Boolean preScroll = false;
        public Show () {}
        public Show(Boolean preScroll) {
            this.preScroll = preScroll;
        }
    }

    @Url( value = "employee_registration")
    public static class Create {
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
