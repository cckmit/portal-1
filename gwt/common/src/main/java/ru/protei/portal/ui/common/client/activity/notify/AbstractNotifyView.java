package ru.protei.portal.ui.common.client.activity.notify;

import com.google.gwt.user.client.ui.IsWidget;

/**
 *  Абстрактное уведомление
 */
public interface AbstractNotifyView extends IsWidget {

    void setActivity(AbstractNotifyActivity activity);

    /**
     * Задает текст уведомления
     */
    void setMessage( String text );

    /**
     * Задает заголовок уведомления
     */
    //void setTitle( String title );

    /**
     * Задает тип уведомления
     */
    void setType( String type );
}
