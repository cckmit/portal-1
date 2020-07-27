package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Company;
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
        @Omit
        public Boolean preScroll = false;
        public Show () {}
        public Show (Boolean preScroll) {
            this.preScroll = preScroll;
        }
    }

    /**
     * Показать таблицу котактов
     */
    public static class ShowConciseTable {
        public ShowConciseTable(HasWidgets parent, Long companyId, Boolean embedded) {
            this.parent = parent;
            this.companyId = companyId;
            this.embedded = embedded;
        }

        public ShowConciseTable(HasWidgets parent, Long companyId) {
            this(parent, companyId, false);
        }

        public ShowConciseTable readOnly() {
            this.editable = false;
            return this;
        }

        public HasWidgets parent;
        public Long companyId;
        public boolean editable = true;
        public boolean embedded;
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
        public Long companyId;
        @Omit
        public Company company;
        public String origin;
        @Omit
        public Runnable backEvent;

        public Edit() { this.id = null; }
        public Edit (Long id, Long companyId) {
            this.id = id;
            this.companyId = companyId;
        }
        public Edit (Long id, Company company, String origin) {
            this.id = id;
            this.company = company;
            this.origin = origin;
        }

        public static Edit byId (Long id) {
            return new Edit(id, null);
        }

        public static Edit newItem (EntityOption option) {
            return new Edit(null, option != null ? option.getId() : null);
        }

        public Edit withBackEvent(Runnable backEvent) {
            this.backEvent = backEvent;
            return this;
        }
    }
}
