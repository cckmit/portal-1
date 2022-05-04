package ru.protei.portal.test.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.test.api.En_WorkerRecordTestApiValidationResult;
import ru.protei.portal.test.api.model.WorkerRecordTestAPI;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public Result<WorkerRecordTestAPI> addWorker(WorkerRecordTestAPI workerRecordTestAPI) {

        En_WorkerRecordTestApiValidationResult validationResult = validateWorkerRecord(workerRecordTestAPI);
        if (validationResult != En_WorkerRecordTestApiValidationResult.OK) {
            return error(En_ResultStatus.VALIDATION_ERROR, validationResult.getMessage());
        }

        String mail = workerRecordTestAPI.getMail();
        if (isEmailExist(mail)) {
            return error(En_ResultStatus.EMPLOYEE_EMAIL_ALREADY_EXIST);
        }

        String login = workerRecordTestAPI.getLogin();
        if (isLoginExist(login)) {
            return error(En_ResultStatus.LOGIN_ALREADY_EXIST);
        }

        Person person = createPerson(workerRecordTestAPI);
        Long personId = personDAO.persist(person);

        WorkerEntry workerEntry = createWorkerEntry(workerRecordTestAPI, personId);
        workerEntryDAO.persist(workerEntry);

        UserLogin userLogin = createUserLogin(workerRecordTestAPI, personId);
        userLoginDAO.persist(userLogin);
        jdbcManyRelationsHelper.persist(userLogin, WorkerRecordTestAPI.Columns.ROLES);

        return ok(workerRecordTestAPI);
    }

    private Person createPerson(WorkerRecordTestAPI workerRecordTestAPI) {
        Person person = new Person();
        person.setCompanyId(workerRecordTestAPI.getCompanyId());
        person.setFirstName(workerRecordTestAPI.getFirstName());
        person.setLastName(workerRecordTestAPI.getLastName());
        person.setDisplayName(workerRecordTestAPI.getLastName() + " " + workerRecordTestAPI.getFirstName());
        person.setGender(En_Gender.parse(workerRecordTestAPI.getSex()));
        person.setBirthday(workerRecordTestAPI.getBirthday());
        person.setCreated(new Date());
        person.setIpAddress(workerRecordTestAPI.getIp());
        person.setCreator(WorkerRecordTestAPI.Constansts.PERSON_CREATOR);
        ContactItem contactItem = new ContactItem(workerRecordTestAPI.getMail(), En_ContactItemType.EMAIL);
        person.getContactItems().add(contactItem);
        return person;
    }

    private WorkerEntry createWorkerEntry(WorkerRecordTestAPI workerRecordTestAPI, Long personId) {
        WorkerEntry workerEntry = new WorkerEntry();
        workerEntry.setCreated(new Date());
        workerEntry.setPersonId(personId);
        workerEntry.setDepartmentId(workerRecordTestAPI.getDepartmentId());
        workerEntry.setCompanyId(workerRecordTestAPI.getCompanyId());
        workerEntry.setPositionId(workerRecordTestAPI.getPositionId());
        workerEntry.setActiveFlag(WorkerRecordTestAPI.Constansts.ACTIVE_FLAG);
        workerEntry.setContractAgreement(workerRecordTestAPI.getContractAgreement());
        return workerEntry;
    }

    private UserLogin createUserLogin(WorkerRecordTestAPI workerRecordTestAPI, Long personId) {
        UserLogin userLogin = new UserLogin();
        userLogin.setUlogin(workerRecordTestAPI.getLogin());
        userLogin.setUpass(DigestUtils.md5DigestAsHex(workerRecordTestAPI.getPassword().trim().getBytes()));
        userLogin.setAuthType(En_AuthType.LOCAL);
        userLogin.setInfo(workerRecordTestAPI.getFirstName() + " " + workerRecordTestAPI.getLastName());
        userLogin.setPersonId(personId);
        userLogin.setCreated(new Date());
        userLogin.setAdminStateId(En_AdminState.LOCKED.getId());
        Set<UserRole> userRoles = workerRecordTestAPI.getRoleIds().stream().map(UserRole::new).collect(Collectors.toSet());
        userLogin.setRoles(userRoles);
        return userLogin;
    }

    private En_WorkerRecordTestApiValidationResult validateWorkerRecord(WorkerRecordTestAPI workerRecordTestAPI) {

        if (HelperFunc.isEmpty(workerRecordTestAPI.getFirstName())) {
            return En_WorkerRecordTestApiValidationResult.EMPTY_FIRST_NAME;
        }

        if (HelperFunc.isEmpty(workerRecordTestAPI.getLastName())) {
            return En_WorkerRecordTestApiValidationResult.EMPTY_LAST_NAME;
        }

        if (HelperFunc.isEmpty(workerRecordTestAPI.getSex())) {
            return En_WorkerRecordTestApiValidationResult.EMPTY_GENDER;
        }

        if (workerRecordTestAPI.getBirthday() == null) {
            return En_WorkerRecordTestApiValidationResult.EMPTY_BIRTHDAY;
        }

        if (HelperFunc.isEmpty(workerRecordTestAPI.getPhone())) {
            return En_WorkerRecordTestApiValidationResult.EMPTY_PHONE;
        }

        if (HelperFunc.isEmpty(workerRecordTestAPI.getMail())) {
            return En_WorkerRecordTestApiValidationResult.EMPTY_MAIL;
        }

        if (workerRecordTestAPI.getContractAgreement() == null) {
            return En_WorkerRecordTestApiValidationResult.EMPTY_CONTRACT_AGREEMENT;
        }

        if (workerRecordTestAPI.getCompanyId() == null) {
            return En_WorkerRecordTestApiValidationResult.EMPTY_COMPANY_ID;
        }

        if (workerRecordTestAPI.getDepartmentId() == null) {
            return En_WorkerRecordTestApiValidationResult.EMPTY_DEPARTMENT_ID;
        }

        CompanyDepartment department = companyDepartmentDAO.get(workerRecordTestAPI.getDepartmentId());
        if (department == null) {
            return En_WorkerRecordTestApiValidationResult.NOT_EXIST_DEPARTMENT_ID;
        }

        if (workerRecordTestAPI.getPositionId() == null) {
            return En_WorkerRecordTestApiValidationResult.EMPTY_POSITION_ID;
        }

        WorkerPosition workerPosition = workerPositionDAO.get(workerRecordTestAPI.getPositionId());
        if (workerPosition == null) {
            return En_WorkerRecordTestApiValidationResult.NOT_EXIST_POSITION_ID;
        }

        if (HelperFunc.isEmpty(workerRecordTestAPI.getIp())) {
            return En_WorkerRecordTestApiValidationResult.EMPTY_IP;
        }

        if (!workerRecordTestAPI.getIp().trim().matches("^[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}$")) {
            return En_WorkerRecordTestApiValidationResult.INVALID_FORMAT_IP;
        }

        if (HelperFunc.isEmpty(workerRecordTestAPI.getInn())) {
            return En_WorkerRecordTestApiValidationResult.EMPTY_INN;
        }

        if (workerRecordTestAPI.getInn().length() != WorkerRecordTestAPI.Constansts.INN_LENGTH) {
            return En_WorkerRecordTestApiValidationResult.INVALID_FORMAT_INN;
        }

        if (HelperFunc.isEmpty(workerRecordTestAPI.getLocale())) {
            return En_WorkerRecordTestApiValidationResult.EMPTY_LOCALE;
        }

        if (!WorkerRecordTestAPI.Constansts.DEFAULT_LOCALE_LIST.contains(workerRecordTestAPI.getLocale())) {
            return En_WorkerRecordTestApiValidationResult.INVALID_FORMAT_LOCALE;
        }

        if (workerRecordTestAPI.isFired() == null) {
            return En_WorkerRecordTestApiValidationResult.EMPTY_IS_FIRED;
        }

        if (HelperFunc.isEmpty(workerRecordTestAPI.getLogin())) {
            return En_WorkerRecordTestApiValidationResult.EMPTY_LOGIN;
        }

        if (HelperFunc.isEmpty(workerRecordTestAPI.getPassword())) {
            return En_WorkerRecordTestApiValidationResult.EMPTY_PASSWORD;
        }

        if (workerRecordTestAPI.getRoleIds() == null) {
            return En_WorkerRecordTestApiValidationResult.EMPTY_ROLE_IDS;
        }

        return En_WorkerRecordTestApiValidationResult.OK;

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
