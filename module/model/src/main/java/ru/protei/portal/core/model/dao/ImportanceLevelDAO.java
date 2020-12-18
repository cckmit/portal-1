package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.winter.jdbc.JdbcDAO;

import java.util.List;

public interface ImportanceLevelDAO extends JdbcDAO<Integer, ImportanceLevel> {
    List<ImportanceLevel> getImportanceLevelsByCompanyId(Long companyId);
}
