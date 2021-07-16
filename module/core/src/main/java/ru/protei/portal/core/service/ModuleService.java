package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Module;

import java.util.List;

public interface ModuleService {
    Result<List<Module>> getModulesByKitId(AuthToken token, Long kitId);
}
