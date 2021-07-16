package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.ModuleDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Module;

import java.util.List;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class ModuleServiceImpl implements ModuleService {
    @Autowired
    ModuleDAO moduleDAO;

    @Override
    public Result<List<Module>> getModulesByKitId(AuthToken token, Long kitId) {
        if (kitId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return ok(moduleDAO.getListByCondition("kit_id = ?", kitId));
    }


}
