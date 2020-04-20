package ru.protei.portal.ui.employee.client.activity.edit;

import ru.protei.portal.core.model.ent.CompanyDepartment;
import ru.protei.portal.core.model.ent.WorkerPosition;

public interface AbstractEmployeeEditActivity {
    void onSaveClicked();
    void onCancelClicked();
    void onFireClicked();
    void validateLimitedFields();
    void onCompanySelected();

    void onAddCompanyDepartmentClicked();
    void onEditCompanyDepartmentClicked(Long id, String name);

    void onAddWorkerPositionClicked();
    void onEditWorkerPositionClicked(WorkerPosition workerPosition);

    void onChangedCompanyDepartment(Long departmentId);
    void onChangedWorkerPosition(Long positionId);
}
