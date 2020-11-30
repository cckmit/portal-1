package ru.protei.portal.ui.common.client.activity.dialogdetails;

/**
 * Абстрактная активность для карточки диалогового окна
 */
public interface AbstractDialogDetailsActivity {

    /**
     * Нажата кнопка "Удалить"
     * */
    default void onRemoveClicked() {}

    default void onAdditionalClicked() {}

    /**
     * Нажата кнопка "Сохранить"
     */
    void onSaveClicked();

    /**
     * Нажата кнопка "Отмена"
     */
    void onCancelClicked();
}
