package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.PersonShortViewDAO;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.List;

public class PersonShortViewDAOImpl extends PortalBaseJdbcDAO<PersonShortView> implements PersonShortViewDAO {

    @Autowired
    EmployeeSqlBuilder employeeSqlBuilder;

    @Autowired
    PersonSqlBuilder personSqlBuilder;
    @Autowired
    ContactSqlBuilder contactSqlBuilder;
    @Autowired
    AccountingEmployeeSqlBuilder accountingEmployeeSqlBuilder;

    @Override
    public List<PersonShortView> getPersonsShortView( PersonQuery query ) {
        return getList( personSqlBuilder.makeParameters( query ) );
    }

    @Override
    public List<PersonShortView> getAccountingEmployees(List<String> ids, List<String> departmentIds) {
        return getList(accountingEmployeeSqlBuilder.makeParameters(ids, departmentIds));
    }

    @Override
    public List<PersonShortView> getEmployees( EmployeeQuery query ) {
        return getList(employeeSqlBuilder.makeParameters( query ));
    }

    @Override
    public List<PersonShortView> getContacts( ContactQuery query) {
        return getList( contactSqlBuilder.makeParameters( query ) );
    }

    @Override
    public PersonShortView getCommonManagerByProductId( Long productId ) {
        return getByCondition( "person.id = (SELECT dev_unit.common_manager_id FROM dev_unit WHERE dev_unit.ID = ?)", productId );
    }

}
