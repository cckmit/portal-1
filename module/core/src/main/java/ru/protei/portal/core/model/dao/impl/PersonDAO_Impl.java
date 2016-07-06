package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CompanyGroupHomeDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.CompanyHomeGroupItem;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.Collections;
import java.util.List;

/**
 * Created by michael on 04.04.16.
 */
public class PersonDAO_Impl extends PortalBaseJdbcDAO<Person> implements PersonDAO {

    @Autowired
    CompanyGroupHomeDAO groupHomeDAO;

    @Override
    public List<Person> getEmployeesAll() {

        StringBuilder expr = new StringBuilder();
        List<CompanyHomeGroupItem> members = groupHomeDAO.getAll();

        if (members == null || members.isEmpty())
            return Collections.emptyList();

        for (CompanyHomeGroupItem item : members) {
            if (expr.length() == 0) {
                expr.append("company_id in (");
            }
            else
                expr.append(",");

            expr.append(item.getCompanyId());
        }

        expr.append(")");
        expr.append(" and displayPosition is not null");

        return getListByCondition(expr.toString(), new JdbcSort(JdbcSort.Direction.ASC,"displayname"), new Object[]{});
    }
}
