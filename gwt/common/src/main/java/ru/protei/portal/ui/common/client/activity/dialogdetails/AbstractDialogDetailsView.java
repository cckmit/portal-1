package ru.protei.portal.ui.common.client.activity.dialogdetails;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстрактный вид для карточки диалогового окна
 */
public interface AbstractDialogDetailsView extends IsWidget {

    void setActivity( AbstractDialogDetailsActivity activity );

    /**
     * Возвращает контенер для содержимого карточки
     */
    HasWidgets getBodyContainer();

    void showPopup();

    void hidePopup();

    HasVisibility removeButtonVisibility();

    HasVisibility saveButtonVisibility();

    /**
     * Установить заголовок окна детализации
     */
    void setHeader( String value );

    void addStyleName( String value );

    void setSaveOnEnterClick(boolean isSaveOnEnterClick);
}
