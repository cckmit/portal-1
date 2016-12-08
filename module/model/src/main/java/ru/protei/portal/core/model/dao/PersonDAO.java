package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;

/**
 * Created by michael on 04.04.16.
 */
public interface PersonDAO extends PortalBaseDAO<Person> {

    List<Person> getEmployeesAll();

    Person getEmployee( long id );

    List<Person> getEmployees (EmployeeQuery query);

    boolean isEmployee (Person p);

    List<Person> getContacts (ContactQuery query);

    Person getContact (long id);

    @SqlConditionBuilder
    SqlCondition createContactSqlCondition(ContactQuery query);

    @SqlConditionBuilder
    SqlCondition createEmployeeSqlCondition(EmployeeQuery query);

}
