package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.List;

public interface PersonShortViewDAO extends PortalBaseDAO<PersonShortView> {

    List<PersonShortView> getPersonsShortView( PersonQuery query );

    List<PersonShortView> getAccountEmployees( List<String> ids, List<String> DepartmentIds );

    List<PersonShortView> getEmployees( EmployeeQuery query );

    List<PersonShortView> getContacts( ContactQuery query);

    PersonShortView getCommonManagerByProductId( Long productId );
}
