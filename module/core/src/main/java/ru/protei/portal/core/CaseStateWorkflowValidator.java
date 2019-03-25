package ru.protei.portal.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.ent.CaseStateWorkflow;
import ru.protei.portal.core.model.util.CaseStateWorkflowUtil;
import ru.protei.portal.core.service.CaseStateWorkflowService;

public class CaseStateWorkflowValidator {

    public boolean isCaseStateTransitionValid(En_CaseStateWorkflow workflow, En_CaseState caseStateFrom, En_CaseState caseStateTo) {
        if (caseStateFrom == caseStateTo) {
            return true;
        }
        CoreResponse<CaseStateWorkflow> response = caseStateWorkflowService.getWorkflow(workflow);
        if (response.isError()) {
            log.error("Failed to get case state workflow, status={}", response.getStatus());
            return false;
        }
        return CaseStateWorkflowUtil.isCaseStateTransitionValid(response.getData(), caseStateFrom, caseStateTo);
    }

    @Autowired
    CaseStateWorkflowService caseStateWorkflowService;

    private static Logger log = LoggerFactory.getLogger(CaseStateWorkflowValidator.class);
}
