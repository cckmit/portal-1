package ru.protei.portal.ui.common.client.events;

/**
 * Контейнер событий окна подтверждения.
 */
public class ConfirmDialogEvents {
    /**
     * Показать окно подтверждения с заданным текстом и подписью кнопки подтверждения.
     */
    public static class Show {
        public Show(String text, Runnable confirmAction) {
            this.confirmAction = confirmAction;
            this.text = text;
        }

        public Show(String text, String confirmButtonText, Runnable confirmAction) {
            this.confirmAction = confirmAction;
            this.text = text;
            this.confirmButtonText = confirmButtonText;
        }

        public String text;
        public String confirmButtonText;
        public Runnable confirmAction;
    }
}
