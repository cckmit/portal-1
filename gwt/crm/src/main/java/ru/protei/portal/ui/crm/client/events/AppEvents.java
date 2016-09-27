package ru.protei.portal.ui.crm.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;

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
     *   Отобразить Auth
     */
    public static class Logout {
        public Logout() { }
    }

    /**
     *   Отобразить App
     */
    @Url( value = "app", primary = true )
    public static class Show {
        public Show(){}
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
}
