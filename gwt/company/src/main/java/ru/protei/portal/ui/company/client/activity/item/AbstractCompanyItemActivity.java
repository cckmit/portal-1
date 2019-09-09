package ru.protei.portal.ui.company.client.activity.item;


/**
 * Активность компании
 */
public interface AbstractCompanyItemActivity {

    void onEditClicked( AbstractCompanyItemView itemView );

    void onFavoriteClicked( AbstractCompanyItemView itemView );

    void onPreviewClicked( AbstractCompanyItemView itemView );

    void onLockClicked(AbstractCompanyItemView itemView);
}
