package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.view.EntityOption;

/**
 * Created by turik on 28.10.16.
 */
public class RoleEvents {

    /**
     * Показать контакты
     */
    @Url( value = "roles", primary = true )
    public static class Show {

        public Show () {}

    }

    /**
     * Показать таблицу котактов
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
     * Показать превью контакта
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

        public Integer id;

        public Edit() {}

        public Edit( Integer id ) { this.id = id; }
    }
}