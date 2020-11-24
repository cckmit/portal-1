package ru.protei.portal.ui.account.client.activity.edit;

/**
 * Активность создания и редактирования учетной записи
 */
public interface AbstractAccountEditActivity {
    void onSaveClicked();
    void onCancelClicked();
    void onChangeLogin();
    void onSearchChanged();
}
