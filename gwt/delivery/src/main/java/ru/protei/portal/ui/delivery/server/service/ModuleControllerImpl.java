package ru.protei.portal.ui.delivery.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Module;
import ru.protei.portal.core.service.ModuleService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.ModuleController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;
import static ru.protei.portal.ui.common.server.ServiceUtils.getAuthToken;

@Service("ModuleController")
public class ModuleControllerImpl implements ModuleController {

    private static final Logger log = LoggerFactory.getLogger(ModuleControllerImpl.class);

    @Autowired
    ModuleService moduleService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Override
    public Map<Module, List<Module>> getModulesByKitId(Long kitId) throws RequestFailedException {
        Result<Map<Module, List<Module>>> result = moduleService.getModulesByKitId(getAuthToken(sessionService, httpServletRequest), kitId);
        return checkResultAndGetData(result);
    }

    @Override
    public Set<Long> removeModules(Set<Long> ids) throws RequestFailedException {
        log.info("removeModules(): modulesIds={}", ids);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<Set<Long>> response = moduleService.removeModules(token, ids);
        log.info("removeModules(): modulesIds={}, {}", ids, response.isOk() ? "ok" : response.getStatus());
        return checkResultAndGetData(response);
    }
}
