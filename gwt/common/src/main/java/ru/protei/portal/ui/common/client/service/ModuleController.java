package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Module;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;
import java.util.Map;

@RemoteServiceRelativePath( "springGwtServices/ModuleController" )
public interface ModuleController extends RemoteService {

    Module getModule(Long id) throws RequestFailedException;

    Map<Module, List<Module>> getModulesByKitId(Long kitId) throws RequestFailedException;

    void updateNameAndDescription(CaseNameAndDescriptionChangeRequest changeRequest)  throws RequestFailedException;

    Module updateMeta(Module meta) throws RequestFailedException;

    Module saveModule(Module module);
}
