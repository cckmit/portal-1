package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CompanyImportanceItem;

import java.util.List;

public interface CompanyImportanceItemDAO extends PortalBaseDAO<CompanyImportanceItem> {

    List<CompanyImportanceItem> getSortedImportanceLevels(Long companyId);
}
