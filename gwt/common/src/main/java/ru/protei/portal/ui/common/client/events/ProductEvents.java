package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;

/**
 * События по продуктам
 */
public class ProductEvents {

    /**
     * Показать grid продуктов
     */
    @Url( value = "products", primary = true )
    public static class Show {}

    /**
     * Показать карточку продукта
     */
    public static class ShowPreview {

        public ShowPreview( HasWidgets parent, DevUnit product, boolean isWatchForScroll, boolean isShouldWrap ) {
            this.parent = parent;
            this.product = product;
            this.isWatchForScroll = isWatchForScroll;
            this.isShouldWrap = isShouldWrap;
        }

        public HasWidgets parent;
        public DevUnit product;
        public boolean isWatchForScroll;
        public boolean isShouldWrap;
    }

    @Url( value = "product" )
    public static class Edit {

        public Edit () {
            productId = null;
        }

        public Edit (Long id) {
            this.productId = id;
        }

        public Long productId;
    }

    public static class ProductListChanged {}

    public static class QuickCreate {
        public QuickCreate(HasWidgets parent) {
            this.parent = parent;
        }
        public HasWidgets parent;
    }

    /**
     * Установить проект
     */
    public static class Set {

        public Set(DevUnit product) {
            this.product = product;
        }

        public DevUnit product;
    }
}
