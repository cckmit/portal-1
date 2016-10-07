package ru.protei.portal.ui.company.client.activity.item;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyListActivity;

/**
 * Created by turik on 30.09.16.
 */
public interface AbstractCompanyItemActivity {

    void onMenuClicked( AbstractCompanyItemView itemView );

    void onFavoriteClicked( AbstractCompanyItemView itemView );
}
