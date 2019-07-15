package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;

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

        public ShowPreview( HasWidgets parent, EmployeeShortView employee ) {
            this.parent = parent;
            this.employee = employee;
        }

        public HasWidgets parent;
        public EmployeeShortView employee;
    }

    public static class ShowDefinite {
        public ShowDefinite (ViewType type, Widget filter, EmployeeQuery query) {
            this.viewType = type;
            this.filter = filter;
            this.query = query;
        }

        public ViewType viewType;
        public Widget filter;
        public EmployeeQuery query;
    }

    public static class UpdateData {
        public UpdateData ( ViewType type, EmployeeQuery query ) {
            this.viewType = type;
            this.query = query;
        }

        public ViewType viewType;
        public EmployeeQuery query;
    }
}
