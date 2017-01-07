package ru.protei.portal.ui.region.client.activity.item;

/**
 *  Абстракция активности карточки региона
 */
public interface AbstractRegionItemActivity {

    void onFavoriteClicked( AbstractRegionItemView itemView );

    void onEditClicked( AbstractRegionItemView itemView );

    void onPreviewClicked( AbstractRegionItemView itemView );
}
