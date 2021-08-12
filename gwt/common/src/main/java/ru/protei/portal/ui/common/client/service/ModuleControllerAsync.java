package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Module;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ModuleControllerAsync {
    void getModulesByKitId(Long kitId, AsyncCallback<Map<Module, List<Module>>> async);

    void removeModules(Long kitId, Set<Long> modulesIds, AsyncCallback<Set<Long>> async);
}
