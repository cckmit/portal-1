package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.view.EntityOption;

/**
 * События модуля - классификтор оборудования
 */
public class EquipmentEvents {

    /**
     * Показать
     */
    @Url( value = "equipment", primary = true )
    public static class Show {

        public Show () {}

    }

    /**
     * Показать таблицу оборудования
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
     * Показать превью оборудования
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

    @Url( value = "equipment", primary = false )
    public static class Edit {

        public Long id;
        public Long classifierId;

        public Edit() { this.id = null; }
        public Edit (Long id, Long companyId) {
            this.id = id;
            this.classifierId = companyId;
        }

        public static Edit byId (Long id) {
            return new Edit(id, null);
        }

        public static Edit newItem (EntityOption option) {
            return new Edit(null, option != null ? option.getId() : null);
        }
    }
}