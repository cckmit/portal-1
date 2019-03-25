package ru.protei.portal.core.model.util;

import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.CaseStateWorkflow;
import ru.protei.portal.core.model.ent.CaseStateWorkflowLink;

import java.util.List;
import java.util.Objects;

public class CaseStateWorkflowUtil {

    public static boolean isCaseStateTransitionValid(CaseStateWorkflow workflow, En_CaseState caseStateFrom, En_CaseState caseStateTo) {
        Objects.requireNonNull(workflow, "Workflow should not be null");

        if (caseStateFrom == caseStateTo) {
            return true;
        }

        if (workflow.getId() == En_CaseStateWorkflow.NO_WORKFLOW.getId()) {
            return true;
        }

        List<CaseStateWorkflowLink> links = workflow.getCaseStateWorkflowLinks();
        for (CaseStateWorkflowLink link : links) {
            if (link.getCaseStateFrom() == caseStateFrom && link.getCaseStateTo() == caseStateTo) {
                return true;
            }
        }

        return false;
    }

    public static En_CaseStateWorkflow recognizeWorkflow(CaseObject caseObject) {
        Objects.requireNonNull(caseObject, "Case object should not be null");

        if (false) {
            // TODO paste criteria for NX_JIRA
            return En_CaseStateWorkflow.NX_JIRA;
        }

        return En_CaseStateWorkflow.NO_WORKFLOW;
    }
}
