package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.view.EntityOption;

/**
 * Created by turik on 28.10.16.
 */
public class ContactEvents {

    @Url( value = "contacts", primary = true )
    public static class Show {

        public Show () {}

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

        public static Edit newItem (EntityOption option) {
            return new Edit(null, option != null ? option.getId() : null);
        }
    }
}

