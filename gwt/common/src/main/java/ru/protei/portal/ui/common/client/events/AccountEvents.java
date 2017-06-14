package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.UserLogin;

/**
 * События по аккаунтам
 */
public class AccountEvents {
    /**
     * Показать аккаунты
     */
    @Url( value = "accounts", primary = true )
    public static class Show {

        public Show () {}

    }

    /**
     * Показать превью аккаунта
     */
    public static class ShowPreview {

        public ShowPreview ( HasWidgets parent, UserLogin account )
        {
            this.parent = parent;
            this.account = account;
        }

        public UserLogin account;
        public HasWidgets parent;

    }

    @Url( value = "account", primary = false )
    public static class Edit {

        public Long id;

        public Edit() { this.id = null; }
        public Edit ( Long id ) {
            this.id = id;
        }
    }
}
