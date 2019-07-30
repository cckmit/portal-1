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

    @Override
    public CompanyHomeGroupItem getByExternalCode(String externalCode) {
        return getByCondition ("external_code=?", externalCode);
    }

    public Long mainCompanyId() {
        CompanyHomeGroupItem item = getByCondition("mainId is null");
        return item == null ? null : item.getCompanyId();
    }
}
