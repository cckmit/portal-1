package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;

/**
 * События уведомления
 */
public class NotifyEvents {

    /**
     * Инициализации
     */
    public static class Init {

        public Init (HasWidgets parent) {
            this.parent = parent;
        }

        public HasWidgets parent;
    }

    /**
     *  Отобразить уведомление
     */
    public static class Show {

        public Show( String message) {
            this.message = message;
            this.type = NotifyType.INFO;
        }

        public Show( String message, NotifyType type){
            this.message = message;
            this.type = type;
        }

        public String message;
        public NotifyType type;
    }

    public enum NotifyType {
        INFO ("info"),
        ERROR ("warning"),
        SUCCESS ("success");

        private final String style;

        NotifyType (String type) { this.style = type; }

        public String getStyle() { return style; }
    }

}
