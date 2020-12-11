package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.ImportanceLevel;

import java.util.List;

public interface ImportanceLevelDAO extends PortalBaseDAO<ImportanceLevel> {
    List<ImportanceLevel> getImportanceLevelsByCompanyId(Long companyId);
}
