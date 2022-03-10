package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.ent.EmployeeRegistrationShortView;
import ru.protei.portal.core.model.query.EmployeeRegistrationQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

@RemoteServiceRelativePath("springGwtServices/EmployeeRegistrationController")
public interface EmployeeRegistrationController extends RemoteService {

    SearchResult<EmployeeRegistration> getEmployeeRegistrations(EmployeeRegistrationQuery query) throws RequestFailedException;

    EmployeeRegistration getEmployeeRegistration(Long id) throws RequestFailedException;

    EmployeeRegistrationShortView getEmployeeRegistrationShortView(Long id) throws RequestFailedException;

    Long createEmployeeRegistration(EmployeeRegistration employeeRegistration) throws RequestFailedException;

    Long updateEmployeeRegistration(EmployeeRegistrationShortView employeeRegistration) throws RequestFailedException;

    EmployeeRegistration completeProbationPeriod(Long id) throws RequestFailedException;
}
