package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CompanyGroupHomeDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.*;

import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

public class PersonDAO_Impl extends PortalBaseJdbcDAO<Person> implements PersonDAO {

    @Autowired
    CompanyGroupHomeDAO groupHomeDAO;

    @Autowired
    EmployeeSqlBuilder employeeSqlBuilder;

    @Autowired
    PersonSqlBuilder personSqlBuilder;
    @Autowired
    ContactSqlBuilder contactSqlBuilder;

    @Override
    public boolean existsByLegacyId(Long legacyId) {
        return countByExpression("old_id=?", legacyId) > 0L;
    }

    @Override
    public Map<Long, Long> mapLegacyId() {
        Map<Long, Long> result = new HashMap<>();

        partialGetListByCondition("old_id is not null",Collections.emptyList(),"id", "old_id")
                .forEach(person -> result.put(person.getOldId(), person.getId()));

        return result;
    }

    @Override
    public Person findContactByName(long companyId, String displayName) {
        SqlCondition sql = new SqlCondition().build((condition, args) -> {
            condition.append("person.company_id = ?");
            args.add(companyId);

            condition.append(" and person.displayName like ?");
            args.add(HelperFunc.makeLikeArg(displayName, false));
        });

        return findFirst(sql);
    }

    @Override
    public Person getEmployeeByOldId(long id) {
        return getByCondition("person.old_id=?", id);
    }

    @Override
    public SearchResult<Person> getEmployeesSearchResult(EmployeeQuery query) {
        JdbcQueryParameters parameters = employeeSqlBuilder.makeParameters(query);
        return getSearchResult(parameters);
    }

    @Override
    public List<Person> getEmployees(EmployeeQuery query) {
        JdbcQueryParameters parameters = employeeSqlBuilder.makeParameters( query );
        return getList(parameters);
    }

    @Override
    public SearchResult<Person> getContactsSearchResult(ContactQuery query) {
        JdbcQueryParameters parameters = contactSqlBuilder.makeParameters( query );
        return getSearchResult(parameters);
    }

    @Override
    public SearchResult<Person> getPersonsSearchResult(PersonQuery query) {
        return getSearchResult( personSqlBuilder.makeParameters( query ) );
    }

    @Override
    public List<Person> getPersons(PersonQuery query) {
        return getList( personSqlBuilder.makeParameters( query ) );
    }

    @Override
    public List<Person> getPersonsByEmail(String email) {
        Condition cnd = query().where("person.id").in(query()
                .select("cip.person_id").from("contact_item_person AS cip")
                .where("cip.contact_item_id").in(query()
                        .select("ci.id").from("contact_item AS ci")
                        .where("ci.item_type").equal(En_ContactItemType.EMAIL.getId())
                        .and("ci.value").equal(email).asQuery()
                ).asQuery()
        );

        return getListByCondition(cnd.getSqlCondition(), cnd.getSqlParameters());
    }

    @Override
    public Person getCommonManagerByProductId(Long productId) {
        return getByCondition("person.id = (SELECT dev_unit.common_manager_id FROM dev_unit WHERE dev_unit.ID = ?)", productId);
    }

    /**
     * Query Condition builders
     */

    private Person findFirst(SqlCondition sql) {
        JdbcQueryParameters parameters = new JdbcQueryParameters();
        parameters.withCondition(sql.condition, sql.args);
        parameters.withOffset(0);
        parameters.withLimit(1);

        List<Person> rlist = getList (parameters);

        return rlist != null && !rlist.isEmpty() ? rlist.get(0) : null;
    }
}
