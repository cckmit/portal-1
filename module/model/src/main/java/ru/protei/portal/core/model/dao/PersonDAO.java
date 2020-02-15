package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.net.Inet4Address;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by michael on 04.04.16.
 */
public interface PersonDAO extends PortalBaseDAO<Person> {


    List<Person> getEmployeesAll();

    Person getEmployee(long id);

    Person getEmployeeByOldId(long id);

    SearchResult<Person> getEmployeesSearchResult(EmployeeQuery query);

    List<Person> getEmployees(EmployeeQuery query);

    boolean isEmployee(Person p);


    SearchResult<Person> getContactsSearchResult(ContactQuery query);

    List<Person> getContacts(ContactQuery query);

    Person getContact(long id);

    Person findContactByEmail(long companyId, String email);

    Person findContactByName(long companyId, String displayName);


    SearchResult<Person> getPersonsSearchResult(PersonQuery query);

    List<Person> getPersons(PersonQuery query);

    Person getDepartmentHeadByWorkerEntryId(Long departmentId);


    @SqlConditionBuilder
    SqlCondition createContactSqlCondition(ContactQuery query);

    @SqlConditionBuilder
    SqlCondition createEmployeeSqlCondition(EmployeeQuery query);

    @SqlConditionBuilder
    SqlCondition createPersonSqlCondition(PersonQuery query);


    default Person createNewPerson(Long companyId) throws Exception {
        Person person = new Person();
        person.setCreated(new Date());
        person.setCreator("portal-api@" + Inet4Address.getLocalHost().getHostAddress());
        person.setCompanyId(companyId);
        return person;
    }

    boolean existsByLegacyId (Long legacyId);

    /**
     * Возвращает соответствие между ID в старом портале и текущим ID записи в новой БД
     * В качестве ключа используется ID в старой базе
     * @return
     */
    Map<Long,Long> mapLegacyId ();
}
