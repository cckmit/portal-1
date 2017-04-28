package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CompanyGroupHomeDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CompanyHomeGroupItem;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.EntityCache;
import ru.protei.portal.core.utils.EntitySelector;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by michael on 04.04.16.
 */
public class PersonDAO_Impl extends PortalBaseJdbcDAO<Person> implements PersonDAO {

    @Autowired
    CompanyGroupHomeDAO groupHomeDAO;

    EntityCache<CompanyHomeGroupItem> homeGroupCache;


    @PostConstruct
    protected void postConstruct () {
        homeGroupCache = new EntityCache<CompanyHomeGroupItem>(groupHomeDAO, TimeUnit.MINUTES.toMillis(10));
    }

    @Override
    public Person findContactByName(long companyId, String displayName) {
        SqlCondition sql = new SqlCondition().build((condition, args) -> {
            condition.append("Person.company_id = ?");
            args.add(companyId);

            condition.append("and Person.displayName like ?");
            args.add(HelperFunc.makeLikeArg(displayName, false));
        });

        return findFirst(sql);
    }

    private Person findFirst(SqlCondition sql) {
        JdbcQueryParameters parameters = new JdbcQueryParameters();
        parameters.withCondition(sql.condition, sql.args);
        parameters.withOffset(0);
        parameters.withLimit(1);

        List<Person> rlist = getList (parameters);

        return rlist != null && !rlist.isEmpty() ? rlist.get(0) : null;
    }

    @Override
    public Person findContactByEmail(long companyId, String email) {
        SqlCondition sql = new SqlCondition().build((condition, args) -> {
            condition.append("Person.company_id = ?");
            args.add(companyId);

            condition.append("and Person.contactInfo like ?");
            args.add(HelperFunc.makeLikeArg(email, true));
        });

        return findFirst(sql);
    }

    @Override
    public Person getEmployee( long id ) {
        final Person person = get(id);

        return person != null && ifPersonIsEmployee(person) ? person : null;
    }

    @Override
    public boolean isEmployee(Person p) {
        return ifPersonIsEmployee(p);
    }

    private boolean ifPersonIsEmployee(final Person employee) {
        return homeGroupCache.exists(new EntitySelector<CompanyHomeGroupItem>() {
            public boolean matches(CompanyHomeGroupItem entity) {
                return employee.getCompanyId().equals(entity.getCompanyId());
            }
        });
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
    public List<Person> getEmployees(EmployeeQuery query) {

        return listByQuery( query );
    }

    private StringBuilder buildHomeCompanyFilter() {
        return buildHomeCompanyFilter(false);
    }

    private StringBuilder buildHomeCompanyFilter(boolean inverse) {
        final StringBuilder expr = new StringBuilder();

        homeGroupCache.walkThrough(new EntityCache.Visitor<CompanyHomeGroupItem>() {
            @Override
            public void visitEntry(long idx, CompanyHomeGroupItem item) {
                if (idx == 0) {
                    expr.append("company_id ").append(inverse ? "not in" : "in").append (" (");
                }
                else
                    expr.append(",");

                expr.append(item.getCompanyId());
            }
        });

        if (expr.length() > 0)
            expr.append(")");

        return expr;
    }


    @Override
    public List<Person> getContacts(ContactQuery query) {
        if (query.getCompanyId() != null && homeGroupCache.exists(entity -> entity.getCompanyId().equals(query.getCompanyId())))
            return Collections.emptyList();

        return listByQuery(query);
    }

    @Override
    public Person getContact(long id) {
        Person p = get(id);

        return ifPersonIsEmployee(p) ? null : p;
    }


    /**
     * Query Condition builders
     */

    @Override
    @SqlConditionBuilder
    public SqlCondition createContactSqlCondition(ContactQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("Person.company_id not in (select companyId from company_group_home)");

            if (query.getCompanyId() != null) {
                condition.append(" and Person.company_id = ?");
                args.add(query.getCompanyId());
            }

            if (query.getFired() != null) {
                condition.append(" and Person.isfired=?");
                args.add(query.getFired() ? 1 : 0);
            }

            if (HelperFunc.isLikeRequired(query.getSearchString())) {
                condition.append(" and (Person.displayName like ? or Person.contactInfo like ?)");
                String likeArg = HelperFunc.makeLikeArg(query.getSearchString(), true);

                args.add(likeArg);
                args.add(likeArg);
            }
        });
    }

    @Override
    @SqlConditionBuilder
    public SqlCondition createEmployeeSqlCondition(EmployeeQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("Person.company_id in (select companyId from company_group_home)");

            if (query.getFired() != null) {
                condition.append(" and Person.isfired=?");
                args.add(query.getFired() ? 1 : 0);
            }

            if (HelperFunc.isLikeRequired(query.getSearchString())) {
                condition.append(" and (Person.displayName like ? or Person.contactInfo like ?)");
                String likeArg = HelperFunc.makeLikeArg(query.getSearchString(), true);

                args.add(likeArg);
                args.add(likeArg);
            }
        });
    }
}
