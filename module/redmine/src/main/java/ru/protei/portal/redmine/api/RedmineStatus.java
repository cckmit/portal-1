package ru.protei.portal.redmine.api;

import ru.protei.portal.core.model.dict.En_CaseState;

import java.util.Arrays;

import static ru.protei.portal.core.model.dict.En_CaseState.*;

public enum RedmineStatus {
    NEW ("Новая", CREATED),
    IN_PROGRESS ("В работе", ACTIVE),
    STOPPED("Остановлена", PAUSED),
    SOLVED ("Решена", DONE),
    FEEDBACK ("Обратная связь", DISCUSS),
    REJECTED ("Отказ", REOPENED),
    CLOSED ("Закрыта", VERIFIED),
    BACKLOG("Запланирована", PLANNED);

    RedmineStatus (String code, En_CaseState caseState) {
        this.redmineCode = code;
        this.caseState = caseState;
    }

    private final String redmineCode;
    private final En_CaseState caseState;

    public static RedmineStatus getByCaseState(En_CaseState state) {
        return Arrays.stream(RedmineStatus.values()).filter(x -> x.caseState.equals(state)).findFirst().get();
    }

    public String getRedmineCode() {
        return redmineCode;
    }

    public En_CaseState getCaseState() {
        return caseState;
    }

    public static RedmineStatus parse (String code) {
        if (code == null || code.isEmpty())
            return null;

        for (RedmineStatus it : RedmineStatus.values())
            if (it.redmineCode.equalsIgnoreCase(code))
                return it;

        return null;
    }
}
