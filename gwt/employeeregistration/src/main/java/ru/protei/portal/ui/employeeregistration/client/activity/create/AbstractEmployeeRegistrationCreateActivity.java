package ru.protei.portal.ui.employeeregistration.client.activity.create;

public interface AbstractEmployeeRegistrationCreateActivity {

    void onSaveClicked();

    void onCancelClicked();

    void validateLimitedFields();

    void onHeadOfDepartmentChanged();

    void onCompanySelected();
}
