package ru.protei.portal.ui.employee.client.activity.edit;

public interface AbstractEmployeeEditActivity {
    void onSaveClicked();
    void onCancelClicked();
    void onFireClicked();
    void validateLimitedFields();
    void onCompanySelected();
    void onGenderSelected();

    void onAddCompanyDepartmentClicked();
    void onEditCompanyDepartmentClicked(Long id, String name);

    void onAddWorkerPositionClicked();
    void onEditWorkerPositionClicked(Long id, String name);
}
