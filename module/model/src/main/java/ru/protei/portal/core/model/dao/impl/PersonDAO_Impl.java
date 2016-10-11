package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CompanyGroupHomeDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.CompanyHomeGroupItem;
import ru.protei.portal.core.model.ent.Person;

import ru.protei.portal.core.utils.EntityCache;
import ru.protei.portal.core.utils.EntitySelector;
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

        final StringBuilder expr = new StringBuilder();

        homeGroupCache.walkThrough(new EntityCache.Visitor<CompanyHomeGroupItem>() {
            @Override
            public void visitEntry(long idx, CompanyHomeGroupItem item) {
                if (idx == 0) {
                    expr.append("company_id in (");
                }
                else
                    expr.append(",");

                expr.append(item.getCompanyId());
            }
        });

        if (expr.length() == 0)
            return Collections.emptyList();

        expr.append(")");
        expr.append(" and displayPosition is not null");

        return getListByCondition(expr.toString(), new JdbcSort(JdbcSort.Direction.ASC,"displayname"), new Object[]{});
    }
}
