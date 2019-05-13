package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.query.EmployeeRegistrationQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public interface EmployeeRegistrationService {

    @Privileged(En_Privilege.EMPLOYEE_REGISTRATION_VIEW)
    CoreResponse<SearchResult<EmployeeRegistration>> getEmployeeRegistrations(AuthToken token, EmployeeRegistrationQuery query);

    @Privileged(En_Privilege.EMPLOYEE_REGISTRATION_VIEW)
    CoreResponse<EmployeeRegistration> getEmployeeRegistration(AuthToken token, Long id);

    @Auditable(En_AuditType.EMPLOYEE_REGISTRATION_CREATE)
    @Privileged(requireAny = En_Privilege.EMPLOYEE_REGISTRATION_CREATE)
    CoreResponse<Long> createEmployeeRegistration(AuthToken token, EmployeeRegistration employeeRegistration);

}
