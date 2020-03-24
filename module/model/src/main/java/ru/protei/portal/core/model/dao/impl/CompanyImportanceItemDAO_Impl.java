package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CompanyImportanceItemDAO;
import ru.protei.portal.core.model.ent.CompanyImportanceItem;

import java.util.List;

public class CompanyImportanceItemDAO_Impl extends PortalBaseJdbcDAO<CompanyImportanceItem> implements CompanyImportanceItemDAO {

    @Override
    public List<Integer> getImportanceLevels(Long companyId) {
        String query = "select importance_level_id from company_importance_item where company_id=? order by `order_number`";
        return jdbcTemplate.queryForList(query, Integer.class, companyId);
    }
}
