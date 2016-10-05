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
        public Show( String title, String message, String type){
            this.title = title;
            this.message = message;
            this.type = type;
        }

        public String title;
        public String message;
        public String type;
    }

}
