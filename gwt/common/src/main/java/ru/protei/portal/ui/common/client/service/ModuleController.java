package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Module;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

@RemoteServiceRelativePath( "springGwtServices/ModuleController" )
public interface ModuleController extends RemoteService {
    List<Module> getModulesByKitId(Long kitId) throws RequestFailedException;
}
