package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Person;

import java.util.List;

/**
 * Created by michael on 04.04.16.
 */
public interface PersonDAO extends PortalBaseDAO<Person> {

    public List<Person> getEmployeesAll();

    public Person getEmployeeById (long id);
}
