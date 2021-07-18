package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.ModuleDAO;
import ru.protei.portal.core.model.ent.Module;

import java.util.List;
import java.util.stream.Collectors;


public class ModuleDAO_Impl extends PortalBaseJdbcDAO<Module> implements ModuleDAO {
    @Override
    public List<Module> getListByKitId(Long kitId) {
        List<Module> modules = getListByCondition("kit_id = ?", kitId);

        if (!modules.isEmpty()) {
            List<Module> childModules = getListByCondition("parent_module_id in (?)", convertListToStringIds(modules));
            modules.addAll(childModules);
        }
        return modules;
    }

    private static String convertListToStringIds(List<Module> modules) {
        return modules.stream()
                .map(module -> module.getId().toString())
                .collect(Collectors.joining(","));
    }
}