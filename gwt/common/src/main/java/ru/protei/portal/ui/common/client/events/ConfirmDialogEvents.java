package ru.protei.portal.ui.common.client.events;


/**
 * Контейнер событий окна подтверждения.
 */
public class ConfirmDialogEvents {

    /**
     * Показать окно подтверждения с заданным текстом и подписью кнопки подтверждения.
     */
    public static class Show {
        public interface Action {
            void onConfirm();
            default void onCancel() {}
        }

        public Show(String identity, String text) {
            this.identity = identity;
            this.text = text;
        }

        public Show(String identity, String text, String confirmButtonText) {
            this.identity = identity;
            this.text = text;
            this.confirmButtonText = confirmButtonText;
        }

        public Show(String text, Action action) {
            this.action = action;
            this.text = text;
        }

        public Show(String text, String confirmButtonText, Action action) {
            this.action = action;
            this.text = text;
            this.confirmButtonText = confirmButtonText;
        }

        public String identity;
        public String text;
        public String confirmButtonText;
        public Action action;
    }

    /**
     * Послать событие-подтверждение.
     */
    public static class Confirm {

        public Confirm( String identity ) {
            this.identity = identity;
        }

        public String identity;
    }

    public static class Cancel {
        public Cancel( String identity ) {
            this.identity = identity;
        }

        public String identity;
    }
}
