package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.utils.HelperFunc;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by michael on 01.04.16.
 */
public class CompanyDAO_Impl extends PortalBaseJdbcDAO<Company> implements CompanyDAO {

    @Override
    public List<Company> getListByQuery(CompanyQuery query) {
        StringBuilder condition = new StringBuilder("cname like ?");
        List<Object> args = new ArrayList<Object>(1);
        args.add(HelperFunc.makeLikeArg(query.getSearchString(), true));

        if (query.getGroupId() != null && query.getGroupId() > 0) {
            condition.append(" and id in (select company_id from company_group_item where group_id=?)");
            args.add(query.getGroupId());
        }

        if (query.getCategoryId() != null && query.getCategoryId() > 0) {
            condition.append(" and category_id = ?");
            args.add(query.getCategoryId());
        }


        return getListByCondition(condition.toString(), TypeConverters.createSort(query), args);
    }
}
