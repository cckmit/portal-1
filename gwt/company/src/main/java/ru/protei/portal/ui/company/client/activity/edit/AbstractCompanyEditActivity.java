package ru.protei.portal.ui.company.client.activity.edit;

/**
 * Активность создания и редактирования компании
 */
public interface AbstractCompanyEditActivity {
    void onSaveClicked();
    void onCancelClicked();
    void onChangeCompanyName();
    void onCategoryChanged();
}
