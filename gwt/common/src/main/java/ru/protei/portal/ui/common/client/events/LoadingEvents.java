package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;

/**
 * События нужные для отображения сообщения о загрузке
 */
public class LoadingEvents {
    public static class Show {
        public Show( HasWidgets parent ) {
            this.parent = parent;
        }

        public HasWidgets parent;
    }

    public static class Hide {
        public Hide( HasWidgets parent ) {
            this.parent = parent;
        }

        public HasWidgets parent;
    }
}
