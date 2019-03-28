package ru.protei.portal.ui.common.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseStateWorkflow;
import ru.protei.portal.core.model.struct.CaseStateAndWorkflowList;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.CaseStateWorkflowService;
import ru.protei.portal.ui.common.client.service.CaseStateWorkflowController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service("CaseStateWorkflowController")
public class CaseStateWorkflowControllerImpl implements CaseStateWorkflowController {

    @Override
    public CaseStateAndWorkflowList getCaseStateAndWorkflowList() throws RequestFailedException {

        ServiceUtils.getDescriptor(sessionService, httpServletRequest);

        CoreResponse<List<CaseStateWorkflow>> result1 = caseStateWorkflowService.getWorkflowList();
        List<CaseStateWorkflow> caseStateWorkflowList = ServiceUtils.checkResultAndGetData(result1);

        En_CaseType type = En_CaseType.CRM_SUPPORT;
        CoreResponse<List<En_CaseState>> result2 = caseService.stateList(type);
        List<En_CaseState> caseStateList = ServiceUtils.checkResultAndGetData(result2);

        return new CaseStateAndWorkflowList(caseStateList, caseStateWorkflowList);
    }

    @Override
    public List<CaseStateWorkflow> getWorkflowList() throws RequestFailedException {
        CoreResponse<List<CaseStateWorkflow>> result = caseStateWorkflowService.getWorkflowList();
        return ServiceUtils.checkResultAndGetData(result);
    }

    @Override
    public CaseStateWorkflow getWorkflow(En_CaseStateWorkflow caseStateWorkflow) throws RequestFailedException {
        CoreResponse<CaseStateWorkflow> result = caseStateWorkflowService.getWorkflow(caseStateWorkflow);
        return ServiceUtils.checkResultAndGetData(result);
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    CaseStateWorkflowService caseStateWorkflowService;
    @Autowired
    CaseService caseService;
}
