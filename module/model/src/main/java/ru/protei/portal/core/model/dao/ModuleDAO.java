package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Module;

import java.util.List;

public interface ModuleDAO extends PortalBaseDAO<Module> {
    List<Module> getListByKitId(Long kitId);

    List<String> getSerialNumbersByKitId(Long kitId);
}
