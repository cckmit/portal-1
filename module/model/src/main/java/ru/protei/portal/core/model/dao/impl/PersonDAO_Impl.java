package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CompanyGroupHomeDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CompanyHomeGroupItem;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.utils.EntityCache;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.condition;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

public class PersonDAO_Impl extends PortalBaseJdbcDAO<Person> implements PersonDAO {

    @Autowired
    CompanyGroupHomeDAO groupHomeDAO;

    @Autowired
    EmployeeSqlBuilder employeeSqlBuilder;

    private final static String WORKER_ENTRY_JOIN = "LEFT JOIN worker_entry WE ON WE.personId = person.id";

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
    public Person findContactByEmail(long companyId, String email) {
        SqlCondition sql = new SqlCondition().build((condition, args) -> {
            condition.append("person.company_id = ?");
            args.add(companyId);

            condition.append(" AND person.id IN (");
            condition.append(" SELECT cip.person_id FROM contact_item_person AS cip WHERE cip.contact_item_id IN (");
            condition.append(" SELECT ci.id FROM contact_item AS ci WHERE 1=1");
            condition.append(" AND (ci.item_type = ? and ci.value like ?)");
            args.add(En_ContactItemType.EMAIL.getId());
            args.add(HelperFunc.makeLikeArg(email, true));
            condition.append(" ))");
        });

        return findFirst(sql);
    }

    @Override
    public List<Person> findContactByEmail(String email) {
        SqlCondition sql = new SqlCondition().build((condition, args) -> {
            condition.append(" person.id IN (");
            condition.append(" SELECT cip.person_id FROM contact_item_person AS cip WHERE cip.contact_item_id IN (");
            condition.append(" SELECT ci.id FROM contact_item AS ci WHERE 1=1");
            condition.append(" AND (ci.item_type = ? and ci.value like ?)");
            args.add(En_ContactItemType.EMAIL.getId());
            args.add(HelperFunc.makeLikeArg(email, true));
            condition.append(" ))");
        });

        return getListByCondition(sql.condition, sql.args);
    }

    @Override
    public List<Person> findEmployeeByEmail(String email) {
        SqlCondition sql = new SqlCondition().build((condition, args) -> {
            condition.append(" person.company_id = ?");
            condition.append(" and person.id IN (");
            condition.append(" SELECT cip.person_id FROM contact_item_person AS cip WHERE cip.contact_item_id IN (");
            condition.append(" SELECT ci.id FROM contact_item AS ci WHERE 1=1");
            condition.append(" AND (ci.item_type = ? and ci.value like ?)");
            args.add(CrmConstants.Company.HOME_COMPANY_ID);
            args.add(En_ContactItemType.EMAIL.getId());
            args.add(HelperFunc.makeLikeArg(email, true));
            condition.append(" ))");
        });

        return getListByCondition(sql.condition, sql.args);
    }

    @Override
    public Person getEmployee(long id) {
        final Person person = get(id);

        return person != null && ifPersonIsEmployee(person) ? person : null;
    }

    @Override
    public Person getEmployeeByOldId(long id) {
        Person person = getByCondition("person.old_id=?", id);
        return person != null && ifPersonIsEmployee(person) ? person : null;
    }

    @Override
    public boolean isEmployee(Person p) {
        return ifPersonIsEmployee(p);
    }

    @Override
    public List<Person> getEmployeesAll() {

        final StringBuilder expr = buildHomeCompanyFilter();

        /** no home-company exists, so no employees */
        if (expr.length() == 0)
            return Collections.emptyList();

        expr.append(" and displayPosition is not null");

        return getListByCondition(expr.toString(), new JdbcSort(JdbcSort.Direction.ASC, En_SortField.person_full_name.getFieldName()));
    }

    @Override
    public SearchResult<Person> getEmployeesSearchResult(EmployeeQuery query) {
        JdbcQueryParameters parameters = buildEmployeeJdbcQueryParameters(query);
        return getSearchResult(parameters);
    }

    @Override
    public List<Person> getEmployees(EmployeeQuery query) {
        JdbcQueryParameters parameters = buildEmployeeJdbcQueryParameters(query);
        return getList(parameters);
    }

    @Override
    public SearchResult<Person> getContactsSearchResult(ContactQuery query) {
        if (query.getCompanyId() != null && homeGroupCache().exists(entity -> entity.getCompanyId().equals(query.getCompanyId())))
            return new SearchResult<>(Collections.emptyList());

        return getSearchResultByQuery(query);
    }

    @Override
    public List<Person> getContacts(ContactQuery query) {
        if (query.getCompanyId() != null && homeGroupCache().exists(entity -> entity.getCompanyId().equals(query.getCompanyId())))
            return Collections.emptyList();

        return listByQuery(query);
    }

    @Override
    public Person getContact(long id) {
        Person p = get(id);

        return ifPersonIsEmployee(p) ? null : p;
    }

    @Override
    public SearchResult<Person> getPersonsSearchResult(PersonQuery query) {
        return getSearchResultByQuery(query);
    }

    @Override
    public List<Person> getPersons(PersonQuery query) {
        return listByQuery( query );
    }

    @Override
    public Person getCommonManagerByProductId(Long productId) {
        return getByCondition("person.id = (SELECT dev_unit.common_manager_id FROM dev_unit WHERE dev_unit.ID = ?)", productId);
    }

    /**
     * Query Condition builders
     */

    @Override
    @SqlConditionBuilder
    public SqlCondition createContactSqlCondition(ContactQuery query) {
        Condition cnd = condition()
                .condition( buildHomeCompanyFilter( true ) )
                .and( "person.company_id" ).equal( query.getCompanyId() )
                .and( "person.isfired" ).equal( booleanAsNumber( query.getFired() ) )
                .and( "person.isdeleted" ).equal( booleanAsNumber( query.getDeleted() ) )
                .and( condition()
                        .or("person.displayName").like( query.getSearchString() )
                        .or("person.displayName").like( query.getAlternativeSearchString() )
                );
        return new SqlCondition(cnd.getSqlCondition(), cnd.getSqlParameters());
    }


    @Override
    @SqlConditionBuilder
    public SqlCondition createEmployeeSqlCondition(EmployeeQuery query) {
        return employeeSqlBuilder.createSqlCondition(query);
    }

    @Override
    @SqlConditionBuilder
    public SqlCondition createPersonSqlCondition(PersonQuery query) {
        Condition cnd = condition()
                .and( "person.id" ).in( query.getPersonIds() )
                .and( "person.company_id" ).in( query.getCompanyIds() )
                .and( condition()
                        .or( "person.displayName" ).like( query.getSearchString() )
                )
                .and( "person.isfired" ).equal( booleanAsNumber( query.getFired() ) )
                .and( "person.isdeleted" ).equal( booleanAsNumber( query.getDeleted() ) )
                .and( "person.sex" ).not( query.getPeople() ).equal( query.getPeople() == null ? null : En_Gender.UNDEFINED.getCode() );

        if (query.getHasCaseFilter() != null) {
            cnd.and( "person.id" ).in( query()
                    .select( "distinct(person_id)" ).from( "person_to_case_filter" ) );
        }

        return new SqlCondition( cnd.getSqlCondition(), cnd.getSqlParameters() );
    }

    private JdbcQueryParameters buildEmployeeJdbcQueryParameters(EmployeeQuery query) {
        SqlCondition where = createSqlCondition(query);

        JdbcQueryParameters jdbcQueryParameters = new JdbcQueryParameters().
                withJoins(WORKER_ENTRY_JOIN).
                withCondition(where.condition, where.args).
                withDistinct(true).
                withOffset(query.getOffset()).
                withLimit(query.getLimit()).
                withSort(TypeConverters.createSort(query));

        String havingCondition = makeHavingCondition(query);

        if (StringUtils.isNotEmpty(havingCondition)) {
            jdbcQueryParameters
                    .withGroupBy("id")
                    .withHaving(havingCondition);
        }

        return jdbcQueryParameters;
    }

    private String makeHavingCondition(EmployeeQuery query) {
        String result = "";

        int countId = 0;

        if (HelperFunc.isLikeRequired(query.getWorkPhone())) {
            countId++;
        }

        if (HelperFunc.isLikeRequired(query.getMobilePhone())) {
            countId++;
        }

        if (HelperFunc.isLikeRequired(query.getEmail())) {
            countId++;
        }

        if (countId > 0) {
            result = "count(id) = " + countId;
        }

        return result;
    }

    private boolean ifPersonIsEmployee(final Person employee) {
        return homeGroupCache().exists( entity -> employee.getCompanyId().equals(entity.getCompanyId()) );
    }

    private Person findFirst(SqlCondition sql) {
        JdbcQueryParameters parameters = new JdbcQueryParameters();
        parameters.withCondition(sql.condition, sql.args);
        parameters.withOffset(0);
        parameters.withLimit(1);

        List<Person> rlist = getList (parameters);

        return rlist != null && !rlist.isEmpty() ? rlist.get(0) : null;
    }

    private StringBuilder buildHomeCompanyFilter() {
        return buildHomeCompanyFilter(false);
    }

    private StringBuilder buildHomeCompanyFilter(boolean inverse) {
        final StringBuilder expr = new StringBuilder();

        homeGroupCache().walkThrough( ( idx, item ) -> {
            if (idx == 0) {
                expr.append("person.company_id ").append(inverse ? "not in" : "in").append (" (");
            }
            else
                expr.append(",");

            expr.append(item.getCompanyId());
        } );

        if (expr.length() > 0)
            expr.append(")");

        return expr;
    }

    private EntityCache<CompanyHomeGroupItem> homeGroupCache() {
        if (homeGroupCache == null) {
            homeGroupCache = new EntityCache<>(groupHomeDAO, TimeUnit.MINUTES.toMillis(10));
        }
        return homeGroupCache;
    }

    private EntityCache<CompanyHomeGroupItem> homeGroupCache;
}
