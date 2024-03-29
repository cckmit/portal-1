package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Omit;
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
        public ViewType view;
        @Omit
        public Boolean preScroll = false;
        public Show () {
        }
        public Show(Boolean preScroll) {
            this.preScroll = preScroll;
        }
        public Show(ViewType view, Boolean preScroll) {
            this.view = view;
            this.preScroll = preScroll;
        }
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
    @Url(value = "topbrass", primary = true)
    public static class ShowTopBrass {}

    public static class UpdateData {
        public UpdateData ( ViewType type, EmployeeQuery query ) {
            this.viewType = type;
            this.query = query;
        }

        public ViewType viewType;
        public EmployeeQuery query;
    }

    @Url( value = "employee" )
    public static class Edit {

        public Long id;

        public Edit() { this.id = null; }
        public Edit(Long id) { this.id = id; }
    }

    public static class Update {

        public Long id;

        public Update(Long id) { this.id = id; }
    }

    public static class UpdateDefinite {

        public ViewType viewType;
        public Long id;

        public UpdateDefinite(ViewType type, Long id) {
            this.viewType = type;
            this.id = id;
        }
    }

    @Url(value = "birthdays", primary = true)
    public static class ShowBirthdays {
        public ShowBirthdays() {}
    }

    public static class SelectTab {
        public ViewType view;

        public SelectTab(ViewType view) {
            this.view = view;
        }
    }
}
