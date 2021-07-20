package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.ModuleDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Module;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

        List<Module> modules = moduleDAO.getListByKitId(kitId);

        List<Module> parentModules = modules.stream()
                .filter(module -> module.getParentModuleId() == null)
                .sorted(Comparator.comparing(Module::getSerialNumber))
                .collect(Collectors.toList());
        List<Module> childModules = modules.stream()
                .filter(module -> module.getParentModuleId() != null)
                .sorted(Comparator.comparing(Module::getSerialNumber).reversed())
                .collect(Collectors.toList());

        for (Module childModule : childModules) {
            for (int i = 0; i < parentModules.size(); i++) {
                Module parentModule = parentModules.get(i);
                if (childModule.getParentModuleId().equals(parentModule.getId())) {
                    parentModules.add(i + 1, childModule);
                }
            }
        }

        return ok(parentModules);
    }
}
