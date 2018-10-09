package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.query.EmployeeRegistrationQuery;

import java.util.List;

public interface EmployeeRegistrationControllerAsync {
    void getEmployeeRegistrations(EmployeeRegistrationQuery query, AsyncCallback<List<EmployeeRegistration>> callback);

    void getEmployeeRegistrationCount(EmployeeRegistrationQuery query, AsyncCallback<Integer> callback);

    void getEmployeeRegistration(Long id, AsyncCallback<EmployeeRegistration> callback);

    void createEmployeeRegistration(EmployeeRegistration employeeRegistration, AsyncCallback<Long> callback);

    void getEmployeeRegistrationComments(long id, AsyncCallback<List<CaseComment>> commentsLoadedCallback);
}
