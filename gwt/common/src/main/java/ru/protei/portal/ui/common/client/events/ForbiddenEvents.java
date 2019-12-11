package ru.protei.portal.ui.common.client.events;

public class ForbiddenEvents {
    /**
     * Показать страницу "Доступ запрещен"
     */
    public static class Show {
        public String msg;

        public Show() {
        }

        public Show(String msg) {
            this.msg = msg;
        }
    }
}
