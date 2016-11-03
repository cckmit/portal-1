package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CompanyGroupHomeDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CompanyHomeGroupItem;
import ru.protei.portal.core.model.ent.Person;

import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.utils.EntityCache;
import ru.protei.portal.core.utils.EntitySelector;
import ru.protei.portal.core.utils.HelperFunc;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcSort;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
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
    public Person getEmployeeById(long id) {
        final Person person = get(id);

        return person != null && ifPersonIsEmployee(person) ? person : null;
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

        return getListByCondition(expr.toString(), new JdbcSort(JdbcSort.Direction.ASC, En_SortField.person_full_name.getFieldName()), new Object[]{});
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
    public List<Person> getContactsByQuery(ContactQuery query) {

        if (query.getCompanyId() != null && homeGroupCache.exists(entity -> entity.getCompanyId().equals(query.getCompanyId())))
            return Collections.emptyList();

        StringBuilder filter = new StringBuilder("1=1");
        List<Object> args = new ArrayList<>();

        if (query.getCompanyId() != null) {
            filter.append(" and company_id = ?");
            args.add(query.getCompanyId());
        }
        else {
            StringBuilder hgc =  buildHomeCompanyFilter (true);
            if (hgc.length() > 0) {
                filter.append(" and ").append(hgc);
            }
        }

        if (query.getFired() != null) {
            filter.append(" and isfired=?");
            args.add(query.getFired() ? 1 : 0);
        }

        if (query.getSearchString() != null && !query.getSearchString().trim().isEmpty()) {
            filter.append(" and ( displayName like ? or phone_work like ? or phone_home like ? or phone_mobile like ? )");
            String likeArg = HelperFunc.makeLikeArg(query.getSearchString(), true);
            args.add(likeArg);
            args.add(likeArg);
            args.add(likeArg);
            args.add(likeArg);
        }
        return getListByCondition(filter.toString(),args,query.offset,query.limit,TypeConverters.createSort(query)).getResults();
    }

    @Override
    public Person getContactById( long id ) {
        Person p = get(id);
        return ifPersonIsEmployee(p) ? null : p;
    }
}
