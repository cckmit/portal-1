package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;

/**
 * События по продуктам
 */
public class ProductEvents {

    @Url( value = "products", primary = true )
    public static class Show {

        public Show () {}
    }

    @Url( value = "product", primary = false )
    public static class Edit {

        public Edit () {
            productId = null;
        }

        public Edit (Long id) {
            this.productId = id;
        }

        public Long productId;
    }
}
