package ru.protei.portal.core.model.util;

import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.CaseStateWorkflow;
import ru.protei.portal.core.model.ent.CaseStateWorkflowLink;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CaseStateWorkflowUtil {

    public static boolean isCaseStateTransitionValid(CaseStateWorkflow workflow, CaseState caseStateFrom, CaseState caseStateTo) {

        if (caseStateFrom == caseStateTo) {
            return true;
        }

        if (workflow == null || workflow.isMatched(En_CaseStateWorkflow.NO_WORKFLOW)) {
            return true;
        }

        List<CaseStateWorkflowLink> links = workflow.getCaseStateWorkflowLinks();
        for (CaseStateWorkflowLink link : links) {
            if (link.getCaseStateFrom().isEquals(caseStateFrom)
                    && link.getCaseStateTo().isEquals(caseStateTo)) {
                return true;
            }
        }

        return false;
    }


    public static En_CaseStateWorkflow recognizeWorkflow(String extAppType) {

        if (En_ExtAppType.JIRA.getCode().equals(extAppType)) {
            return En_CaseStateWorkflow.NX_JIRA;
        }

        return En_CaseStateWorkflow.NO_WORKFLOW;
    }

    public static List<Long> EnCaseStatesToIds(Collection<En_CaseState> enCaseStates) {
        return enCaseStates.stream().map(en_caseState -> (long) en_caseState.getId()).collect(Collectors.toList());
    }

    public static Set<CaseState> EnCaseStatesToCaseStateSet(Collection<En_CaseState> enCaseStates) {
        return enCaseStates.stream().map(en_caseState -> new CaseState((long)en_caseState.getId())).collect(Collectors.toSet());
    }
    public static List<CaseState> EnCaseStatesToCaseStateList(Collection<En_CaseState> enCaseStates) {
        return enCaseStates.stream().map(en_caseState -> new CaseState((long)en_caseState.getId())).collect(Collectors.toList());
    }
}
