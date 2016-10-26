package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CompanyGroupDAO;
import ru.protei.portal.core.model.ent.CompanyGroup;

/**
 * Created by michael on 10.10.16.
 */
public class CompanyGroupDAO_Impl extends PortalBaseJdbcDAO<CompanyGroup> implements CompanyGroupDAO {

    @Override
    public boolean checkExistsGroupByName(String name, Long id) {

        StringBuilder condition = new StringBuilder(" group_name like ? ");

        if (id != null) {
            condition.append(" and id != ? ");
            return checkExistsByCondition(condition.toString(), name, id);
        }

        return checkExistsByCondition(condition.toString(), name);
    }
}
