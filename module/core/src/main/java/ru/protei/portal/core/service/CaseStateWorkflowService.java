package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.ent.CaseStateWorkflow;

import java.util.List;

public interface CaseStateWorkflowService {

    Result<List<CaseStateWorkflow>> getWorkflowList();

    Result<CaseStateWorkflow> getWorkflow( En_CaseStateWorkflow caseStateWorkflow);
}
