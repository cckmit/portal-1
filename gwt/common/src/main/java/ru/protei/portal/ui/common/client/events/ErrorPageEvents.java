package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;

public class ErrorPageEvents {
    /**
     * Показать страницу "Доступ запрещен"
     */
    public static class ShowForbidden {
        public String msg;
        public HasWidgets container;
        @Deprecated
        public ShowForbidden() {}
        public ShowForbidden(HasWidgets container) { this.container = container; }
        public ShowForbidden(HasWidgets container, String msg) {
            this.container = container;
            this.msg = msg;
        }
    }

    /**
     * Показать страницу "Объект не найден"
     */
    public static class ShowNotFound {
        public String msg;
        public HasWidgets container;
        public ShowNotFound(HasWidgets container) { this.container = container; }
        public ShowNotFound(HasWidgets container, String msg) {
            this.container = container;
            this.msg = msg;
        }
    }
}
