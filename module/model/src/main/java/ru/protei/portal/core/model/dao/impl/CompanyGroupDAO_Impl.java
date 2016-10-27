package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CompanyGroupDAO;
import ru.protei.portal.core.model.ent.CompanyGroup;

/**
 * Created by michael on 10.10.16.
 */
public class CompanyGroupDAO_Impl extends PortalBaseJdbcDAO<CompanyGroup> implements CompanyGroupDAO {

    @Override
    public CompanyGroup getGroupByName(String name) {
        return getByCondition(" group_name=? ", name);
    }
}
