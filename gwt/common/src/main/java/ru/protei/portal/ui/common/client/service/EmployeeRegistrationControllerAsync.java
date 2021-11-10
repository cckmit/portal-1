package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.ent.EmployeeRegistrationShortView;
import ru.protei.portal.core.model.query.EmployeeRegistrationQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

public interface EmployeeRegistrationControllerAsync {

    void getEmployeeRegistrations(EmployeeRegistrationQuery query, AsyncCallback<SearchResult<EmployeeRegistration>> callback);

    void getEmployeeRegistration(Long id, AsyncCallback<EmployeeRegistration> callback);

    void createEmployeeRegistration(EmployeeRegistration employeeRegistration, AsyncCallback<Long> callback);

    void updateEmployeeRegistration(EmployeeRegistrationShortView employeeRegistration, AsyncCallback<Long> async);

    void getEmployeeRegistrationShortView(Long id, AsyncCallback<EmployeeRegistrationShortView> async);

    void completeProbationPeriod(Long id, AsyncCallback<EmployeeRegistration> async);
}
