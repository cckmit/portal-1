package ru.protei.portal.ui.company.client.activity.edit;

/**
 * Активность создания и редактирования компании
 */
public interface AbstractCompanyEditActivity {
    void onStateChanged();

    void onSaveClicked();
    void onCancelClicked();
    void onChangeCompanyName();
    void onAddTagClicked();
}
