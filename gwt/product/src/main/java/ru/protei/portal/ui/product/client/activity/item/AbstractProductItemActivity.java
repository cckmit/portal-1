package ru.protei.portal.ui.product.client.activity.item;

/**
 *  Абстракция активности карточки продукта
 */
public interface AbstractProductItemActivity {

    void onUpdateClicked( AbstractProductItemView itemView  );

    void onMenuClicked( AbstractProductItemView itemView );
}
