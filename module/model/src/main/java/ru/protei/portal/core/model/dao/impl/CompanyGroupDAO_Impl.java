package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CompanyGroupDAO;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CompanyGroupQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;

/**
 * Created by michael on 10.10.16.
 */
public class CompanyGroupDAO_Impl extends PortalBaseJdbcDAO<CompanyGroup> implements CompanyGroupDAO {

    @Override
    public CompanyGroup getGroupByName(String name) {
        return getByCondition(" group_name=? ", name);
    }

    @Override
    public List<CompanyGroup> getListByQuery(CompanyGroupQuery query) {
        return listByQuery(query);
    }


    @SqlConditionBuilder
    public SqlCondition createSqlCondition(CompanyGroupQuery query) {
        return  new SqlCondition().build((condition, args) -> {
            if (HelperFunc.isLikeRequired(query.getSearchString())) {
                condition.append("group_name like ?");
                args.add(HelperFunc.makeLikeArg(query.getSearchString()));
            }
        });
    }

}
