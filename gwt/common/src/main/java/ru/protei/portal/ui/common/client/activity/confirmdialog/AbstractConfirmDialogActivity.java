package ru.protei.portal.ui.common.client.activity.confirmdialog;

/**
 * Абстракция активности окна подтверждения действия.
 */
public interface AbstractConfirmDialogActivity {

    /**
     * Нажатие на кнопку подтверждения действия.
     */
    void onConfirmClicked();

    void onCancelClicked();
}
