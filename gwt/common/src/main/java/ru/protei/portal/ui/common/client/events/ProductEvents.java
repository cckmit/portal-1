package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.DevUnit;

/**
 * События по продуктам
 */
public class ProductEvents {

    /**
     * Показать grid продуктов
     */
    @Url( value = "products", primary = true )
    public static class Show {

        public Show () {}
    }

    /**
     * Показать карточку продукта
     */
    public static class ShowPreview {

        public ShowPreview( HasWidgets parent, DevUnit product, boolean isWatchForScroll ) {
            this.parent = parent;
            this.product = product;
            this.isWatchForScroll = isWatchForScroll;
        }

        public HasWidgets parent;
        public DevUnit product;
        public boolean isWatchForScroll;
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

    public static class ChangeProduct{
        public long productId;

        public ChangeProduct(long productId) {
            this.productId = productId;
        }
    }

    public static class ChangeModel {}
}
