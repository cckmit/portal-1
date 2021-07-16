package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Module;

import java.util.List;

public interface ModuleControllerAsync {
    void getModulesByKitId(Long kitId, AsyncCallback<List<Module>> async);
}
