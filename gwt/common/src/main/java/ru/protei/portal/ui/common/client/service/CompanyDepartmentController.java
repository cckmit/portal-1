package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.CompanyDepartment;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис по работе с компаниями
 */
@RemoteServiceRelativePath( "springGwtServices/CompanyDepartmentController" )
public interface CompanyDepartmentController extends RemoteService {

    List<CompanyDepartment> getCompanyDepartments(Long companyId) throws RequestFailedException;
    
    Long removeCompanyDepartment(Long companyDepartmentId) throws RequestFailedException;

    Long createCompanyDepartment (CompanyDepartment companyDepartment) throws RequestFailedException;

    Long updateCompanyDepartment (CompanyDepartment companyDepartment) throws RequestFailedException;

}
