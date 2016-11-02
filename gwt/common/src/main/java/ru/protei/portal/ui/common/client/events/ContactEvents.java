package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;

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

        public Edit() { this.id = null; }
        public Edit (Long id) {
            this.id = id;
        }
    }
}

