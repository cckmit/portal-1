package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Module;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ModuleService {
    Result<Map<Module, List<Module>>> getModulesByKitId(AuthToken token, Long kitId);

    Result<Set<Long>> removeModules(AuthToken token, Set<Long> modulesIds);
}
