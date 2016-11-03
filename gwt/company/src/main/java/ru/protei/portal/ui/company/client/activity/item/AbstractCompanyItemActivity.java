package ru.protei.portal.ui.company.client.activity.item;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyListActivity;

/**
 * Активность компании
 */
public interface AbstractCompanyItemActivity {

    void onMenuClicked( AbstractCompanyItemView itemView );

    void onFavoriteClicked( AbstractCompanyItemView itemView );
}
