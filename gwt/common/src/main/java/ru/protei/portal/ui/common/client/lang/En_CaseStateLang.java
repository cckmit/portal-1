package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;

/**
 * Названия статусов
 */
public class En_CaseStateLang {

    public String getStateName(En_CaseState state){
        if(state == null)
            return lang.errUnknownResult();

        switch (state){
            case CREATED: return lang.createdCaseState();
            case OPENED: return lang.openedCaseState();
            case CLOSED: return lang.closedCaseState();
            case PAUSED: return lang.pausedCaseState();
            case VERIFIED: return lang.verifiedCaseState();
            case REOPENED: return lang.reopenedCaseState();
            case SOLVED_NOAP: return lang.solvedNoapCaseState();
            case SOLVED_FIX: return lang.solvedFixCaseState();
            case SOLVED_DUP: return lang.solvedDupCaseState();
            case IGNORED: return lang.ignoredCaseState();
            case ASSIGNED: return lang.assignedCaseState();
            case ESTIMATED: return lang.estimatedCaseState();
            case DISCUSS: return lang.discussCaseState();
            case PLANNED: return lang.plannedCaseState();
            case ACTIVE: return lang.activeCaseState();
            case DONE: return lang.doneCaseState();
            case TEST: return lang.testCaseState();
            case TEST_LOCAL: return lang.testLocalCaseState();
            case TEST_CUST: return lang.testCustCaseState();
            case DESIGN: return lang.designCaseState();
            default:
                return lang.errUnknownResult();
        }
    }

    @Inject
    Lang lang;

}
