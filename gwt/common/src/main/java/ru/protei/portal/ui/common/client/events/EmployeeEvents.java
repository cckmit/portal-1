package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;

public class EmployeeEvents {

    /**
     * Показать сотрудников
     */
    @Url( value = "employees", primary = true )
    public static class Show {

        @Omit
        public Boolean preScroll = false;
        public Show () {}
        public Show(Boolean preScroll) {
            this.preScroll = preScroll;
        }

    }

    /**
     * Показать карточку сотрудника
     */
    public static class ShowPreview {

        public ShowPreview( HasWidgets parent, EmployeeShortView employee, boolean isForTableView ) {
            this.parent = parent;
            this.employee = employee;
            this.isForTableView = isForTableView;
        }

        public HasWidgets parent;
        public EmployeeShortView employee;
        public boolean isForTableView;
    }

    @Url(value = "employee_preview", primary = true)
    public static class ShowFullScreen {
        public ShowFullScreen() {}

        public ShowFullScreen(Long employeeId) {
            this.employeeId = employeeId;
        }

        @Name("id")
        public Long employeeId;
    }

    public static class ShowDefinite {
        public ShowDefinite (ViewType type, Widget filter, EmployeeQuery query, Boolean preScroll) {
            this.viewType = type;
            this.filter = filter;
            this.query = query;
            this.preScroll = preScroll;
        }

        public ViewType viewType;
        public Widget filter;
        public EmployeeQuery query;
        public Boolean preScroll;
    }

    /**
     * Показать руководство
     */
    @Url(value = "topbrass")
    public static class ShowTopBrass {}

    public static class UpdateData {
        public UpdateData ( ViewType type, EmployeeQuery query ) {
            this.viewType = type;
            this.query = query;
        }

        public ViewType viewType;
        public EmployeeQuery query;
    }

    @Url( value = "employee", primary = false )
    public static class Edit {

        public Long id;

        public Edit() { this.id = null; }
        public Edit(Long id) { this.id = id; }
    }
}
