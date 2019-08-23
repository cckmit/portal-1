package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.CaseStateWorkflowDAO;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.CaseStateWorkflow;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.List;
import static ru.protei.portal.api.struct.CoreResponse.error;
import static ru.protei.portal.api.struct.CoreResponse.ok;
public class CaseStateWorkflowServiceImpl implements CaseStateWorkflowService {

    @Override
    public CoreResponse<List<CaseStateWorkflow>> getWorkflowList() {
        List<CaseStateWorkflow> workflowList = caseStateWorkflowDAO.getAll();
        if (workflowList == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }
        jdbcManyRelationsHelper.fill(workflowList, "caseStateWorkflowLinks");
        return ok(workflowList);
    }

    @Override
    public CoreResponse<CaseStateWorkflow> getWorkflow(En_CaseStateWorkflow caseStateWorkflow) {
        CaseStateWorkflow workflow = caseStateWorkflowDAO.get(caseStateWorkflow.getId());
        if (workflow == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }
        jdbcManyRelationsHelper.fill(workflow, "caseStateWorkflowLinks");
        return ok(workflow);
    }

    @Autowired
    CaseStateWorkflowDAO caseStateWorkflowDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
}
