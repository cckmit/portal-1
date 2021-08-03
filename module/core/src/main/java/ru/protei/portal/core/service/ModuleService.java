package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Module;

import java.util.List;
import java.util.Map;

public interface ModuleService {
    Result<Module> getModule(AuthToken token, Long id);
    Result<Map<Module, List<Module>>> getModulesByKitId(AuthToken token, Long kitId);
}
