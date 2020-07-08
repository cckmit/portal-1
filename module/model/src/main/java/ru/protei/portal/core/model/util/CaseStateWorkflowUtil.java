package ru.protei.portal.core.model.util;

import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.ent.CaseStateWorkflow;
import ru.protei.portal.core.model.ent.CaseStateWorkflowLink;

import java.util.List;

public class CaseStateWorkflowUtil {

    public static boolean isCaseStateTransitionValid(CaseStateWorkflow workflow, long caseStateFromId, long caseStateToId) {

        if (caseStateFromId == caseStateToId) {
            return true;
        }

        if (workflow == null || workflow.isMatched(En_CaseStateWorkflow.NO_WORKFLOW)) {
            return true;
        }

        List<CaseStateWorkflowLink> links = workflow.getCaseStateWorkflowLinks();
        for (CaseStateWorkflowLink link : links) {
            if (link.getCaseStateFromId() == caseStateFromId
                    && link.getCaseStateToId() == caseStateToId) {
                return true;
            }
        }

        return false;
    }


    public static En_CaseStateWorkflow recognizeWorkflow(String extAppType) {

        if (En_ExtAppType.JIRA.getCode().equals(extAppType)) {
            return En_CaseStateWorkflow.NX_JIRA;
        }

        if (En_ExtAppType.REDMINE.getCode().equals(extAppType)) {
            return En_CaseStateWorkflow.REDMINE;
        }

        return En_CaseStateWorkflow.NO_WORKFLOW;
    }
}
