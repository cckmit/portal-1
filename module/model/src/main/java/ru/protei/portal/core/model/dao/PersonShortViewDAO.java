package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.winter.jdbc.JdbcDAO;

import java.util.List;

public interface PersonShortViewDAO extends PortalBaseDAO<PersonShortView> {

    List<PersonShortView> getPersonsShortView( PersonQuery query );

    List<PersonShortView> getEmployees( EmployeeQuery query );

    List<PersonShortView> getContacts( ContactQuery query);

    PersonShortView getCommonManagerByProductId( Long productId );
}