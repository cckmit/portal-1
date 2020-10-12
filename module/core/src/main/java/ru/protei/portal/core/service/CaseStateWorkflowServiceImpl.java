package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CaseStateWorkflowDAO;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.CaseStateWorkflow;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.List;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
public class CaseStateWorkflowServiceImpl implements CaseStateWorkflowService {
    @Autowired
    CaseStateWorkflowDAO caseStateWorkflowDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Override
    public Result<List<CaseStateWorkflow>> getWorkflowList() {
        List<CaseStateWorkflow> workflowList = caseStateWorkflowDAO.getAll();
        if (workflowList == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }
        jdbcManyRelationsHelper.fill(workflowList, "caseStateWorkflowLinks");
        return ok(workflowList);
    }

    @Override
    public Result<CaseStateWorkflow> getWorkflow( En_CaseStateWorkflow caseStateWorkflow) {
        CaseStateWorkflow workflow = caseStateWorkflowDAO.get(caseStateWorkflow.getId());
        if (workflow == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }
        jdbcManyRelationsHelper.fill(workflow, "caseStateWorkflowLinks");
        return ok(workflow);
    }
}
