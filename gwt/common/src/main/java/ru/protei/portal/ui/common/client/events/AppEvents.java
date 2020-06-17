package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.ui.common.shared.model.Profile;

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
     * Показать страничку профиля пользователя
     */
    @Url( value = "profile", primary = true )
    public static class ShowProfile {}

    /**
     * Показать общую вкладку профиля пользователя
     */
    public static class ShowProfileGeneral {
        public ShowProfileGeneral(HasWidgets parent) {
            this.parent = parent;
        }

        public HasWidgets parent;
    }

    /**
     * Показать вкладку подписок профиля пользователя
     */
    public static class ShowProfileSubscriptions {
        public ShowProfileSubscriptions(HasWidgets parent) {
            this.parent = parent;
        }

        public HasWidgets parent;
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
