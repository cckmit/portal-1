package ru.protei.portal.ui.company.client.activity.item;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция элемента списка компаний
 */
public interface AbstractCompanyItemView extends IsWidget {

    void setActivity( AbstractCompanyItemActivity activity );

    /**
     * Установить название
     */
    void setName( String name );

    /**
     * Установить тип
     */
    void setType( String type );

    /**
     * Возвращает контейнер для превью компании
     */
    HasWidgets getPreviewContainer();

    /**
     * Установить телефон
     */
    void setPhone( String value );

    /**
     * Установить email
     */
    void setEmail( String value );

    /**
     * Установить сайт
     */
    void setWebsite( String value );
}
