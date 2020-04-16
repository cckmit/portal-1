package ru.protei.portal.ui.employee.client.activity.edit;

import ru.protei.portal.core.model.ent.CompanyDepartment;
import ru.protei.portal.core.model.ent.WorkerPosition;

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

    void onAddWorkerPositionClicked();
    void onEditWorkerPositionClicked(WorkerPosition workerPosition);
}
