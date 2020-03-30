package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CompanyImportanceItemDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.CompanyImportanceItem;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.List;

public class CompanyImportanceItemDAO_Impl extends PortalBaseJdbcDAO<CompanyImportanceItem> implements CompanyImportanceItemDAO {

    @Override
    public List<CompanyImportanceItem> getSortedImportanceLevels(Long companyId) {
        List<CompanyImportanceItem> importanceLevels = getList(new JdbcQueryParameters()
        .withCondition("company_id=?", companyId)
        .withSort(new JdbcSort(JdbcSort.Direction.ASC, "order_number")));
        return importanceLevels;
    }
}
