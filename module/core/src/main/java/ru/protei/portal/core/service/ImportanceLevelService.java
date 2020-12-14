package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.ImportanceLevel;

import java.util.List;

public interface ImportanceLevelService {
    Result<List<ImportanceLevel>> getImportanceLevelsByCompanyId(Long companyId);

    Result<List<ImportanceLevel>> getImportanceLevels();

    Result<ImportanceLevel> getImportanceLevel(Integer importanceLevelId);
}
