package ru.protei.portal.ui.region.client.activity.item;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида карточки региона
 */
public interface AbstractRegionItemView extends IsWidget {

    void setActivity( AbstractRegionItemActivity activity );

    void setName( String name );

    void setNumber( Integer number );
}
