package ru.protei.portal.ui.delivery.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Module;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.ModuleService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.ModuleController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ru.protei.portal.ui.common.server.ServiceUtils.*;

@Service("ModuleController")
public class ModuleControllerImpl implements ModuleController {

    private static final Logger log = LoggerFactory.getLogger(ModuleControllerImpl.class);

    @Autowired
    ModuleService moduleService;

    @Autowired
    CaseService caseService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpRequest;

    @Override
    public Module getModule(Long id) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(moduleService.getModule(token, id));
    }

    @Override
    public Map<Module, List<Module>> getModulesByKitId(Long kitId) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(moduleService.getModulesByKitId(token, kitId));
    }

    @Override
    public Module saveModule(Module module) throws RequestFailedException {
        if (module == null) {
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(moduleService.createModule(token, module));
    }

    @Override
    public void updateNameAndDescription(CaseNameAndDescriptionChangeRequest changeRequest)  throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        checkResult(caseService.updateCaseNameAndDescription(token, changeRequest, En_CaseType.MODULE));
    }

    @Override
    public Module updateMeta(Module meta) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(moduleService.updateMeta(token, meta));
    }

    @Override
    public String generateSerialNumber(Long kitId) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(moduleService.generateSerialNumber(token, kitId));
    }

    @Override
    public Set<Long> removeModules(Long kitId, Set<Long> modulesIds) throws RequestFailedException {
        log.info("removeModules(): modulesIds={}", modulesIds);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        Result<Set<Long>> response = moduleService.removeModules(token, kitId, modulesIds);
        log.info("removeModules(): modulesIds={}, {}", modulesIds, response.isOk() ? "ok" : response.getStatus());
        return checkResultAndGetData(response);
    }
}
