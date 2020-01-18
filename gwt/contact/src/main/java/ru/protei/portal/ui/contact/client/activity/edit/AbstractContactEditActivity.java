package ru.protei.portal.ui.contact.client.activity.edit;

/**
 * Активность создания и редактирования контактного лица
 */
public interface AbstractContactEditActivity {
    void onSaveClicked();
    void onCancelClicked();
    void onFireClicked();
    void onChangeContactLogin();
    void onChangeContactPassword();
    void onChangeSendWelcomeEmail();
    void validateLimitedFields();
    void onCompanySelected();
    void generatePassword();
}
