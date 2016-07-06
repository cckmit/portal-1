package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CompanyGroupHomeDAO;
import ru.protei.portal.core.model.ent.CompanyHomeGroupItem;

/**
 * Created by michael on 06.07.16.
 */
public class CompanyGroupHomeDAO_Impl extends PortalBaseJdbcDAO<CompanyHomeGroupItem> implements CompanyGroupHomeDAO {

    public boolean checkIfHome (Long id) {
        return  getByCondition("companyId=?", id) != null;
    }

}
