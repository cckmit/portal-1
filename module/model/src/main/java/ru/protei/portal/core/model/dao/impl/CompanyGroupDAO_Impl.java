package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CompanyGroupDAO;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.BaseQuery;
import ru.protei.portal.core.utils.TypeConverters;

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
    public List<CompanyGroup> getListByQuery(BaseQuery query) {
        return getListByCondition("group_name like ?", TypeConverters.createSort(query), HelperFunc.makeLikeArg(query.getSearchString(),true));
    }
}
