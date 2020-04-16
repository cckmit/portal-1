package ru.protei.portal.ui.employee.client.activity.edit;

import ru.protei.portal.core.model.ent.CompanyDepartment;

public interface AbstractEmployeeEditActivity {
    void onSaveClicked();
    void onCancelClicked();
    void onFireClicked();
    void onChangeEmployeeLogin();
    void onChangeSendWelcomeEmail();
    void validateLimitedFields();
    void onCompanySelected();
    void onPasswordGenerationClicked();

    void onAddCompanyDepartmentClicked();
    void onEditCompanyDepartmentClicked(CompanyDepartment companyDepartment);
    void onSelectCompanyDepartment(CompanyDepartment companyDepartment);
}
