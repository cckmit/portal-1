package ru.protei.portal.ui.company.client.activity.item;

/**
 * Активность компании
 */
public interface AbstractCompanyItemActivity {

    void onMenuClicked( AbstractCompanyItemView itemView );

    void onFavoriteClicked( AbstractCompanyItemView itemView );
}
