package ru.protei.portal.ui.common.client.activity.confirmdialog;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция представления окна подтверждения действия.
 */
public interface AbstractConfirmDialogView extends IsWidget {

    void setActivity( AbstractConfirmDialogActivity activity );

    /**
     * Задает текст-содержимое окна подтверждения.
     */
    void setText( String text );

    /**
     * Задает текст подписи кнопки подтверждения действия.
     */
    void setConfirmButtonText( String text );

    /**
     * Открыть окно подтверждения в центре экрана.
     */
    void center();

    /**
     * Закрыть окно подтверждения.
     */
    void hide();
}
