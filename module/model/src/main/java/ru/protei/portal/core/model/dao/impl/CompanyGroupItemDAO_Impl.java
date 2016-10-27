package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CompanyGroupItemDAO;
import ru.protei.portal.core.model.ent.CompanyGroupItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 10.10.16.
 */
public class CompanyGroupItemDAO_Impl extends PortalBaseJdbcDAO<CompanyGroupItem> implements CompanyGroupItemDAO {

    @Override
    public List<CompanyGroupItem> getCompanyToGroupLinks(Long companyId, Long groupId) {

        if (companyId == null && groupId == null)
            return new ArrayList<>();

        StringBuilder condition = new StringBuilder();
        List<Object> args = new ArrayList<Object>();
        if (companyId != null) {
            condition.append(" company_id=? ");
            args.add(companyId);
        }
        if (groupId != null) {
            condition.append((companyId != null ? " and " : "") + " group_id=? ");
            args.add(groupId);
        }

        return getListByCondition(condition.toString(), args);
    }
}
