package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.DevUnit;

/**
 * События по регионам
 */
public class RegionEvents {

    /**
     * Показать grid регионов
     */
    @Url( value = "regions", primary = true )
    public static class Show {

        public Show () {}
    }

    /**
     * Показать карточку региона
     */
//    public static class ShowPreview {
//
//        public ShowPreview( HasWidgets parent, DevUnit product ) {
//            this.parent = parent;
//            this.product = product;
//        }
//
//        public HasWidgets parent;
//        public DevUnit product;
//    }

    @Url( value = "region", primary = false )
    public static class Edit {

        public Edit () {
            id = null;
        }

        public Edit (Long id) {
            this.id = id;
        }

        public Long id;
    }

    public static class ChangeModel {}
}
