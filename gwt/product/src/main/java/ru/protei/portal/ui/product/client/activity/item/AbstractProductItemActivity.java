package ru.protei.portal.ui.product.client.activity.item;

import ru.protei.portal.ui.product.client.view.item.ProductItemView;

/**
 *  Абстракция активности карточки продукта
 */
public interface AbstractProductItemActivity {

    void onFavoriteClicked( AbstractProductItemView itemView );

    void onEditClicked( AbstractProductItemView itemView  );

    void onPreviewClicked( AbstractProductItemView itemView );

    void onLockClicked(ProductItemView productItemView);
}
