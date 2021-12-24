package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.net.Inet4Address;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PersonDAO extends PortalBaseDAO<Person> {

    Person getEmployeeByOldId( long id);

    SearchResult<Person> getEmployeesSearchResult(EmployeeQuery query);

    List<Person> getEmployees(EmployeeQuery query);

    SearchResult<Person> getContactsSearchResult(ContactQuery query);

    Person findContactByName(long companyId, String displayName);

    SearchResult<Person> getPersonsSearchResult(PersonQuery query);

    List<Person> getPersons(PersonQuery query);

    Person getCommonManagerByProductId(Long productId);

    Person findEmployeeByParameters(String firstname, String lastname, Date birthday);

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
