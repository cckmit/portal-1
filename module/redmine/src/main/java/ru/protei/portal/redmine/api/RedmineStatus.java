package ru.protei.portal.redmine.api;

import ru.protei.portal.core.model.dict.En_CaseState;

import java.util.Arrays;

import static ru.protei.portal.core.model.dict.En_CaseState.*;

public enum RedmineStatus {
    NEW (1, CREATED),
    IN_PROGRESS (2, ACTIVE),
    OPENED_RM(2, OPENED),
    STOPPED(9, PAUSED),
    SOLVED (3, DONE),
    FEEDBACK (4, DISCUSS),
    REJECTED (6, REOPENED),
    CLOSED (5, VERIFIED),
    BACKLOG(8, PLANNED);

    RedmineStatus (int code, En_CaseState caseState) {
        this.redmineCode = code;
        this.caseState = caseState;
    }

    private final int redmineCode;
    private final En_CaseState caseState;

    public static RedmineStatus getByCaseState(En_CaseState state) {
        return Arrays.stream(RedmineStatus.values()).filter(x -> x.caseState.equals(state)).findFirst().orElse(null);
    }

    public int getRedmineCode() {
        return redmineCode;
    }

    public En_CaseState getCaseState() {
        return caseState;
    }

    public static RedmineStatus find(int code) {
        if (code <= 0)
            return null;

        for (RedmineStatus it : RedmineStatus.values())
            if (it.redmineCode == code)
                return it;

        return null;
    }
}
