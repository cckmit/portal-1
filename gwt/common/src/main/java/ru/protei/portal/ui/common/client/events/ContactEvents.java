package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.Company;

/**
 * Created by turik on 28.10.16.
 */
public class ContactEvents {

    @Url( value = "contacts", primary = true )
    public static class Show {

        public Show () {}

    }

    /**
     * Показать превью контакта
     */
    //@Url( value = "previewcontact", primary = false )
    public static class ShowPreview {

        public ShowPreview() {}

        public ShowPreview ( HasWidgets parent, Person contact )
        {
            this.parent = parent;
            this.contact = contact;
        }

        public Person contact;
        public HasWidgets parent;

    }

    @Url( value = "contact", primary = false )
    public static class Edit {

        public Long id;
        public Long companyId;

        public Edit() { this.id = null; }
        public Edit (Long id, Long companyId) {
            this.id = id;
            this.companyId = companyId;
        }

        public static Edit byId (Long id) {
            return new Edit(id, null);
        }

        public static Edit newItem (Company company) {
            return new Edit(null, company != null ? company.getId() : null);
        }
    }
}