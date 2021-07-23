package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Module;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;
import java.util.Map;

@RemoteServiceRelativePath( "springGwtServices/ModuleController" )
public interface ModuleController extends RemoteService {
    Map<Module, List<Module>> getModulesByKitId(Long kitId) throws RequestFailedException;
}
