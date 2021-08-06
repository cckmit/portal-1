package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.ModuleDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Module;
import ru.protei.portal.core.model.helper.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class ModuleServiceImpl implements ModuleService {

    private static final Logger log = LoggerFactory.getLogger(ModuleServiceImpl.class);

    @Autowired
    ModuleDAO moduleDAO;

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Override
    public Result<Map<Module, List<Module>>> getModulesByKitId(AuthToken token, Long kitId) {
        if (kitId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        List<Module> modules = moduleDAO.getListByKitId(kitId);
        CollectionUtils.emptyIfNull(modules).sort(Comparator.comparing(Module::getSerialNumber));

        return ok(parentToChild(modules));
    }

    private Map<Module, List<Module>> parentToChild(List<Module> modules) {
        return CollectionUtils.stream(modules)
                .filter(module -> module.getParentModuleId() == null)
                .collect(Collectors.toMap(Function.identity(), module -> modules.stream()
                        .filter(m -> Objects.equals(m.getParentModuleId(), module.getId()))
                        .collect(Collectors.toCollection(ArrayList::new)),
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    @Override
    @Transactional
    public Result<Set<Long>> removeModules(AuthToken token, Set<Long> modulesIds) {
        for (Long moduleId: modulesIds) {
            CaseObject caseObject = new CaseObject(moduleId);
            caseObject.setDeleted(true);

            if (!caseObjectDAO.partialMerge(caseObject, CaseObject.Columns.DELETED)) {
                return error(En_ResultStatus.NOT_UPDATED, "Module " + caseObject.getName() + " was not removed");
            }
        }

        return ok(modulesIds);
    }
}
