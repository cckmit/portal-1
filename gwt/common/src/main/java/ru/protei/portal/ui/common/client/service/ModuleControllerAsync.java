package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Module;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;

import java.util.List;
import java.util.Map;

public interface ModuleControllerAsync {
    void getModule(Long id, AsyncCallback<Module> async);

    void getModulesByKitId(Long kitId, AsyncCallback<Map<Module, List<Module>>> async);

    void updateNameAndDescription(CaseNameAndDescriptionChangeRequest changeRequest, AsyncCallback<Void> async);

    void updateMeta(Module meta, AsyncCallback<Module> async);

    void saveModule(Module module, AsyncCallback<Module> withSuccess);

    void generateSerialNumber(Long kitId, AsyncCallback<String> withSuccess);
}
