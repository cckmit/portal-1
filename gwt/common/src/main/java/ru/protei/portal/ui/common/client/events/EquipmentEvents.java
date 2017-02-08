package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.view.EntityOption;

/**
 * События модуля - классификтор оборудования
 */
public class EquipmentEvents {

    /**
     * Показать
     */
    @Url( value = "equipments", primary = true )
    public static class Show {}

    /**
     * Показать превью оборудования
     */
    public static class ShowPreview {

        public ShowPreview ( HasWidgets parent, Equipment equipment )
        {
            this.parent = parent;
            this.equipment = equipment;
        }

        public Equipment equipment;
        public HasWidgets parent;

    }

    @Url( value = "equipment", primary = false )
    public static class Edit {

        public Long id;

        public Edit() { this.id = null; }
        public Edit (Long id) {
            this.id = id;
        }
        public static Edit byId (Long id) {
            return new Edit(id);
        }
    }

    public static class ChangeModel {}
}