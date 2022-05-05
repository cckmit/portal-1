package ru.protei.portal.test.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.test.api.controller.En_WorkerTestApiValidationResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.List;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

@Service
public class WorkerTestApiServiceImpl implements WorkerTestApiService {

    @Autowired
    UserLoginDAO userLoginDAO;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    CompanyDepartmentDAO companyDepartmentDAO;
    @Autowired
    WorkerEntryDAO workerEntryDAO;
    @Autowired
    WorkerPositionDAO workerPositionDAO;
    @Autowired
    ContactItemDAO contactItemDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Transactional
    @Override
    public Result<En_ResultStatus> addWorker(Person person, WorkerEntry workerEntry, UserLogin userLogin) {

        CompanyDepartment department = companyDepartmentDAO.get(workerEntry.getDepartmentId());
        if (department == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS, En_WorkerTestApiValidationResult.UNKNOWN_DEPARTMENT.getMessage());
        }

        WorkerPosition workerPosition = workerPositionDAO.get(workerEntry.getPositionId());
        if (workerPosition == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS, En_WorkerTestApiValidationResult.UNKNOWN_POSITION.getMessage());
        }


        String email = new PlainContactInfoFacade(person.getContactInfo()).getEmail();
        if (isEmailExist(email)) {
            return error(En_ResultStatus.INCORRECT_PARAMS, En_WorkerTestApiValidationResult.EMAIL_ALREADY_EXIST.getMessage());
        }

        String login = userLogin.getUlogin();
        if (isLoginExist(login)) {
            return error(En_ResultStatus.INCORRECT_PARAMS, En_WorkerTestApiValidationResult.LOGIN_ALREADY_EXIST.getMessage());
        }

        Long personId = personDAO.persist(person);
        contactItemDAO.saveOrUpdateBatch(person.getContactItems());
        jdbcManyRelationsHelper.persist(person, Person.Fields.CONTACT_ITEMS);

        workerEntry.setPersonId(personId);
        workerEntryDAO.persist(workerEntry);

        userLogin.setPersonId(personId);
        userLoginDAO.persist(userLogin);
        jdbcManyRelationsHelper.persist(userLogin, UserLogin.Fields.ROLES);

        return ok(En_ResultStatus.OK);
    }

    private boolean isEmailExist(String email) {
        PersonQuery personQuery = new PersonQuery();
        personQuery.setEmail(email.trim());
        List<Person> personList = personDAO.getPersons(personQuery);
        return CollectionUtils.isNotEmpty(personList);
    }

    private boolean isLoginExist(String login) {
        return !userLoginDAO.isUnique(login.trim());
    }
}
