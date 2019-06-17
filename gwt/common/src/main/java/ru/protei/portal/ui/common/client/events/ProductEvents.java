package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;

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

    public static class ShowDefinite {
        public ShowDefinite (ViewType type, Widget filter, ProductQuery query) {
            this.viewType = type;
            this.filter = filter;
            this.query = query;
        }

        public ViewType viewType;
        public Widget filter;
        public ProductQuery query;
    }

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

    public static class ProductListChanged {}

    /**
     * Обновление списка продуктов по фильтру
     */
    public static class UpdateData {
        public UpdateData(ViewType type, ProductQuery query) {
            this.viewType = type;
            this.query = query;
        }

        public ViewType viewType;
        public ProductQuery query;
    }

    public static class QuickCreate {
        public QuickCreate(HasWidgets parent) {
            this.parent = parent;
        }
        public HasWidgets parent;
    }
    public static class QuickCreated {}
}
