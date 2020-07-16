package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.CompanyDepartment;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.List;

public interface CompanyDepartmentControllerAsync {
    void getCompanyDepartments(Long companyId, AsyncCallback<List<CompanyDepartment>> async);

    void getPersonDepartments(Long personId, boolean withParentDepartments, AsyncCallback<List<EntityOption>> async);

    void removeCompanyDepartment(CompanyDepartment companyDepartment, AsyncCallback<Long> async);

    void createCompanyDepartment(CompanyDepartment companyDepartment, AsyncCallback<Long> async);

    void updateCompanyDepartmentName(CompanyDepartment companyDepartment, AsyncCallback<Long> async);
}
