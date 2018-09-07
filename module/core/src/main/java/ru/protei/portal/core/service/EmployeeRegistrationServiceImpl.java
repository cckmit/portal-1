package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.CaseTypeDAO;
import ru.protei.portal.core.model.dao.EmployeeRegistrationDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.query.EmployeeRegistrationQuery;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.Date;
import java.util.List;

public class EmployeeRegistrationServiceImpl implements EmployeeRegistrationService {

    @Autowired
    EmployeeRegistrationDAO employeeRegistrationDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseTypeDAO caseTypeDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Override
    public CoreResponse<Integer> count(AuthToken token, EmployeeRegistrationQuery query) {
        return new CoreResponse<Integer>().success(employeeRegistrationDAO.countByQuery(query));
    }

    @Override
    public CoreResponse<List<EmployeeRegistration>> employeeRegistrationList(AuthToken token, EmployeeRegistrationQuery query) {
        List<EmployeeRegistration> list = employeeRegistrationDAO.getListByQuery(query);
        if (list == null) {
            return new CoreResponse<List<EmployeeRegistration>>().error(En_ResultStatus.INTERNAL_ERROR);
        }
        return new CoreResponse<List<EmployeeRegistration>>().success(list);
    }

    @Override
    public CoreResponse<EmployeeRegistration> getEmployeeRegistration(AuthToken token, Long id) {
        EmployeeRegistration employeeRegistration = employeeRegistrationDAO.get(id);
        if (employeeRegistration == null)
            return new CoreResponse<EmployeeRegistration>().error(En_ResultStatus.NOT_FOUND);

        jdbcManyRelationsHelper.fillAll(employeeRegistration);
        return new CoreResponse<EmployeeRegistration>().success(employeeRegistration);
    }

    @Override
    @Transactional
    public CoreResponse<Long> createEmployeeRegistration(AuthToken token, EmployeeRegistration employeeRegistration) {
        if (employeeRegistration == null)
            return new CoreResponse<Long>().error(En_ResultStatus.INCORRECT_PARAMS);

        CaseObject caseObject = createCaseObjectFromEmployeeRegistration(employeeRegistration);
        Long id = caseObjectDAO.persist(caseObject);
        if (id == null)
            return new CoreResponse<Long>().error(En_ResultStatus.INTERNAL_ERROR);

        employeeRegistration.setId(id);
        employeeRegistrationDAO.persist(employeeRegistration);

        return new CoreResponse<Long>().success(id);
    }

    private CaseObject createCaseObjectFromEmployeeRegistration(EmployeeRegistration employeeRegistration) {
        CaseObject caseObject = new CaseObject();
        caseObject.setCaseType(En_CaseType.EMPLOYEE_REGISTRATION);
        caseObject.setState(En_CaseState.CREATED);
        caseObject.setCaseNumber(caseTypeDAO.generateNextId(En_CaseType.EMPLOYEE_REGISTRATION));
        caseObject.setCreated(new Date());

        caseObject.setInfo(employeeRegistration.getComment());
        caseObject.setInitiatorId(employeeRegistration.getHeadOfDepartmentId());
        caseObject.setCreatorId(employeeRegistration.getCreatorId());
        caseObject.setName(employeeRegistration.getEmployeeFullName());
        return caseObject;
    }
}
