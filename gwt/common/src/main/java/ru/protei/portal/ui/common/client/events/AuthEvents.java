package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.ui.common.shared.model.Profile;

/**
 * События авторизации
 */
public class AuthEvents {

    /**
     * Инициализация контейнера окна авторизации
     */
    public static class Init {
        public Init( HasWidgets parent ) {
            this.parent = parent;
        }

        public HasWidgets parent;
    }

    /**
     * Событие - показать форму входа
     */
    @Url( value = "login", primary = true)
    public static class Show {}


    /**
     * Успешная авторизация пользователя
     */
    public static class Success {
        public Success( Profile profile) { this.profile = profile; }

        public Profile profile;
    }
}
