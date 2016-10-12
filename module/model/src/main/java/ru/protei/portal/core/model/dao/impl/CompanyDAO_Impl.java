package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.List;

/**
 * Created by michael on 01.04.16.
 */
public class CompanyDAO_Impl extends PortalBaseJdbcDAO<Company> implements CompanyDAO {

    public List<Company> getList(String searchExpression, Long groupId, JdbcSort sort) {

        return groupId == null || groupId <= 0 ?
                getListByCondition("cname like ?", sort, searchExpression)
                : getListByCondition("cname like ? and id in (select company_id from company_group_item where group_id=?)",
                sort, searchExpression, groupId);
    }

}
