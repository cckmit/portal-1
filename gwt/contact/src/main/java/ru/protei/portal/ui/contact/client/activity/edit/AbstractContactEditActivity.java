package ru.protei.portal.ui.contact.client.activity.edit;

/**
 * Активность создания и редактирования контактного лица
 */
public interface AbstractContactEditActivity {
    void onSaveClicked();
    void onCancelClicked();
    void onFireClicked();
    void onChangeContactLoginInfo();
    void onChangeSendWelcomeEmail();
    void validateFields();
    void onCompanySelected();
    void onPasswordGenerationClicked();
}
