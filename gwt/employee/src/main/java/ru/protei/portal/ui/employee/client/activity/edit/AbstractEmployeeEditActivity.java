package ru.protei.portal.ui.employee.client.activity.edit;

public interface AbstractEmployeeEditActivity {
    void onSaveClicked();
    void onCancelClicked();
    void onFireClicked();
    void onChangeEmployeeLogin();
    void onChangeSendWelcomeEmail();
    void validateLimitedFields();
    void onCompanySelected();
    void onPasswordGenerationClicked();
}
