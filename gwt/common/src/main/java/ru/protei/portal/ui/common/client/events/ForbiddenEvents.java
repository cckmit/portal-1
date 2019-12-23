package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;

public class ForbiddenEvents {
    /**
     * Показать страницу "Доступ запрещен"
     */
    public static class Show {
        public HasWidgets container;
        public Show() {}
        public Show(HasWidgets container) { this.container = container; }
    }
}
