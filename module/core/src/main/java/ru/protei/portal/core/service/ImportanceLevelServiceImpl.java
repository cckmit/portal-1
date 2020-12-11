package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.ImportanceLevelDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.ImportanceLevel;

import java.util.List;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class ImportanceLevelServiceImpl implements ImportanceLevelService {
    @Autowired
    private ImportanceLevelDAO importanceLevelDAO;

    @Override
    public Result<List<ImportanceLevel>> getImportanceLevelsByCompanyId(Long companyId) {
        if (companyId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return ok(importanceLevelDAO.getImportanceLevelsByCompanyId(companyId));
    }
}
