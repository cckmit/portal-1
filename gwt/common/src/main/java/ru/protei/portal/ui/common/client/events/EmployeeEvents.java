package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Person;

public class EmployeeEvents {

    /**
     * Показать сотрудников
     */
    @Url( value = "employees", primary = true )
    public static class Show {

        public Show () {}

    }

    /**
     * Показать карточку сотрудника
     */
    public static class ShowPreview {

        public ShowPreview( HasWidgets parent, Person employee ) {
            this.parent = parent;
            this.employee = employee;
        }

        public HasWidgets parent;
        public Person employee;
    }
}
