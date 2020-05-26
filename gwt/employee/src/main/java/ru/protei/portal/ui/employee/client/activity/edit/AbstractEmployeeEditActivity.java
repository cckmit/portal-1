package ru.protei.portal.ui.employee.client.activity.edit;

public interface AbstractEmployeeEditActivity {
    void onSaveClicked();
    void onCancelClicked();
    void onFireClicked();
    void validateLimitedFields();
    void checkLastNameChanged();
    void onCompanySelected();
    void onGenderSelected();
    void onAddPositionBtnClicked();

    void onAddCompanyDepartmentClicked();
    void onEditCompanyDepartmentClicked(Long id, String name);

    void onAddWorkerPositionClicked();
    void onEditWorkerPositionClicked(Long id, String name);
}
