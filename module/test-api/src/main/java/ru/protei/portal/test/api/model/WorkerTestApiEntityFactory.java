package ru.protei.portal.test.api.model;

import org.springframework.util.DigestUtils;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.ent.WorkerEntry;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.test.api.model.WorkerRecordTestAPI;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class WorkerTestApiEntityFactory  {
    public static Person createPerson(WorkerRecordTestAPI workerRecordTestAPI) {
        Person person = new Person();
        person.setCompanyId(workerRecordTestAPI.getCompanyId());
        person.setFirstName(workerRecordTestAPI.getFirstName());
        person.setLastName(workerRecordTestAPI.getLastName());
        person.setDisplayName(workerRecordTestAPI.getLastName() + " " + workerRecordTestAPI.getFirstName());
        person.setInn(workerRecordTestAPI.getInn());
        person.setLocale(workerRecordTestAPI.getLocale());
        person.setBirthday(workerRecordTestAPI.getBirthday());
        person.setCreated(new Date());
        person.setIpAddress(workerRecordTestAPI.getIp());
        person.setCreator(WorkerRecordTestAPI.Constansts.PERSON_CREATOR);

        String gender = workerRecordTestAPI.getSex().substring(0, 1);
        person.setGender(En_Gender.parse(gender));

        ContactItem contactItem = new ContactItem(workerRecordTestAPI.getMail(), En_ContactItemType.EMAIL, En_ContactDataAccess.PUBLIC);
        person.getContactItems().add(contactItem);

        return person;
    }

    public static WorkerEntry createWorkerEntry(WorkerRecordTestAPI workerRecordTestAPI) {
        WorkerEntry workerEntry = new WorkerEntry();
        workerEntry.setCreated(new Date());
        workerEntry.setDepartmentId(workerRecordTestAPI.getDepartmentId());
        workerEntry.setCompanyId(workerRecordTestAPI.getCompanyId());
        workerEntry.setPositionId(workerRecordTestAPI.getPositionId());
        workerEntry.setActiveFlag(WorkerRecordTestAPI.Constansts.ACTIVE_FLAG);
        workerEntry.setContractAgreement(workerRecordTestAPI.getContractAgreement());
        return workerEntry;
    }

    public static UserLogin createUserLogin(WorkerRecordTestAPI workerRecordTestAPI) {
        UserLogin userLogin = new UserLogin();
        userLogin.setUlogin(workerRecordTestAPI.getLogin());
        userLogin.setUpass(DigestUtils.md5DigestAsHex(workerRecordTestAPI.getPassword().trim().getBytes()));
        userLogin.setAuthType(En_AuthType.LOCAL);
        userLogin.setInfo(workerRecordTestAPI.getFirstName() + " " + workerRecordTestAPI.getLastName());
        userLogin.setCreated(new Date());
        userLogin.setAdminStateId(En_AdminState.UNLOCKED.getId());
        Set<UserRole> userRoles = workerRecordTestAPI.getRoleIds().stream().map(UserRole::new).collect(Collectors.toSet());
        userLogin.setRoles(userRoles);
        return userLogin;
    }
}
