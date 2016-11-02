package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Person;

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
//    @Url( value = "previewcontact", primary = false )
    public class ShowPreview {

        public ShowPreview ( HasWidgets parent, Person contact )
        {
            this.parent = parent;
            this.contact = contact;
        }

        public Person contact;
        public HasWidgets parent;

    }
}
