package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Событие для App
 */
public class AppEvents {

    public static class Init {

        public Init (HasWidgets parent)
        {
            this.parent = parent;
        }

        public HasWidgets parent;
    }

    /**
     * Отобразить Auth
     */
    public static class Logout {
        public Logout() { }
    }

    /**
     * Инициализация контейнера App
     */
    public static class InitDetails {

        public InitDetails(HasWidgets parent) {
            this.parent = parent;
        }

        public HasWidgets parent;
    }

    public static class InitPanelName {

        public InitPanelName (String panelName) {

            this.panelName = panelName;
        }

        public String panelName;
    }

    /**
     * Инициализирует список страниц, доступных для авторизованного пользователя
     */
    public static class InitPage {
        public InitPage( Object event ) {
            this.event = event;
        }

        public Object event;
    }

}
