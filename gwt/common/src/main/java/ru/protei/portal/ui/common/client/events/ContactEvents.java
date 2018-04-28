package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.view.EntityOption;

/**
 * События по контактным лицам
 */
public class ContactEvents {

    /**
     * Показать контакты
     */
    @Url( value = "contacts", primary = true )
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

        public ShowPreview ( HasWidgets parent, Person contact )
        {
            this.parent = parent;
            this.contact = contact;
        }

        public Person contact;
        public HasWidgets parent;

    }

    /**
     * Показать превью контакта full screen
     */
    @Url( value = "contact_preview", primary = true )
    public static class ShowFullScreen {

        public ShowFullScreen() {}

        public ShowFullScreen ( Long id )
        {
            this.contactId = id;
        }

        @Name( "id" )
        public Long contactId;
    }

    @Url( value = "contact", primary = false )
    public static class Edit {

        public Long id;
        @Name("company")
        public Long companyId;
        public String back;

        public Edit() { this.id = null; }
        public Edit (Long id, Long companyId) {
            this.id = id;
            this.companyId = companyId;
        }
        public Edit (Long id, Long companyId, String back) {
            this.id = id;
            this.companyId = companyId;
            this.back = back;
        }

        public static Edit byId (Long id) {
            return new Edit(id, null);
        }

        public static Edit newItem (EntityOption option) {
            return new Edit(null, option != null ? option.getId() : null);
        }
    }
}