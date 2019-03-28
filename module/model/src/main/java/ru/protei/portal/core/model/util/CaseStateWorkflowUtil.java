package ru.protei.portal.core.model.util;

import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.CaseStateWorkflow;
import ru.protei.portal.core.model.ent.CaseStateWorkflowLink;

import java.util.List;

public class CaseStateWorkflowUtil {

    private static final String CASE_APP_TYPE_JIRA = "jira";

    public static boolean isCaseStateTransitionValid(CaseStateWorkflow workflow, En_CaseState caseStateFrom, En_CaseState caseStateTo) {

        if (caseStateFrom == caseStateTo) {
            return true;
        }

        if (workflow == null || workflow.isMatched(En_CaseStateWorkflow.NO_WORKFLOW)) {
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

        if (caseObject == null) {
            return En_CaseStateWorkflow.NO_WORKFLOW;
        }

        if (CASE_APP_TYPE_JIRA.equals(caseObject.getExtAppType())) {
            return En_CaseStateWorkflow.NX_JIRA;
        }

        return En_CaseStateWorkflow.NO_WORKFLOW;
    }
}
