package ru.protei.portal.ui.company.client.activity.item;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyListActivity;

/**
 * Представление компании
 */
public interface AbstractCompanyItemView extends IsWidget {

    void setActivity( AbstractCompanyItemActivity activity );
    void setName( String name );
    void setType( String type );
}
