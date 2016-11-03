package ru.protei.portal.ui.product.client.activity.item;

/**
 *  Абстракция активности карточки продукта
 */
public interface AbstractProductItemActivity {

    void onMenuClicked( AbstractProductItemView itemView );

    void onEditClicked( AbstractProductItemView itemView  );
}
