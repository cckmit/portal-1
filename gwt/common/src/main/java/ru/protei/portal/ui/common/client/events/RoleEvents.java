package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.UserRole;

/**
 * Created by turik on 28.10.16.
 */
public class RoleEvents {

    /**
     * Показать роли
     */
    @Url( value = "roles", primary = true )
    public static class Show {

        public Show () {}

    }

    /**
     * Показать таблицу ролей
     */
    public static class ShowTable {

        public ShowTable ( HasWidgets parent, Long companyId) {
            this.parent = parent;
            this.companyId = companyId;
        }

        public HasWidgets parent;
        public Long companyId;
    }

    /**
     * Показать превью роли
     */
    public static class ShowPreview {

        public ShowPreview ( HasWidgets parent, UserRole role )
        {
            this.parent = parent;
            this.role = role;
        }

        public UserRole role;
        public HasWidgets parent;
    }


    @Url( value = "role" )
    public static class Edit {

        public Long id;

        public Edit() {}

        public Edit( Long id ) { this.id = id; }
    }

    /**
     * Добавление / изменение / удаление ролей
     */
    public static class ChangeModel {}
}