package ru.protei.portal.ui.common.client.events;

public class ForbiddenEvents {
    /**
     * Показать страницу "Доступ запрещен"
     */
    public static class Show {
        public Show(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        public String errorMsg;
    }
}
