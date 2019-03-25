package ru.protei.portal.ui.common.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.ent.CaseStateWorkflow;
import ru.protei.portal.core.service.CaseStateWorkflowService;
import ru.protei.portal.ui.common.client.service.CaseStateWorkflowController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@Service("CaseStateWorkflowController")
public class CaseStateWorkflowControllerImpl implements CaseStateWorkflowController {

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
    CaseStateWorkflowService caseStateWorkflowService;
}
