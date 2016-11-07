package ru.protei.portal.ui.product.client.activity.item;

/**
 *  Абстракция активности карточки продукта
 */
public interface AbstractProductItemActivity {

    void onFavoriteClicked( AbstractProductItemView itemView );

    void onEditClicked( AbstractProductItemView itemView  );

    void onPreviewClicked( AbstractProductItemView itemView );
}
