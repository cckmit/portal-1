package ru.protei.portal.hpsm.api;

import ru.protei.portal.core.model.dict.En_CaseState;

import java.util.Arrays;

/**
 * Created by michael on 24.04.17.
 */

public enum HpsmStatus {

    NEW ("Новый", En_CaseState.CREATED),
    REJECTED ("Отклонен", En_CaseState.IGNORED),
    REGISTERED ("Зарегистрирован", En_CaseState.CREATED),
    IN_PROGRESS ("В работе", En_CaseState.ACTIVE),
    INFO_REQUEST ("Запрос информации", En_CaseState.INFO_REQUEST),
    WORKAROUND ("Предоставлено временное решение", En_CaseState.WORKAROUND),
    SOLVED ("Предоставлено постоянное решение", En_CaseState.DONE),
    TEST_WA ("Проверка временного решения", En_CaseState.TEST_CUST),
    TEST_SOLUTION ("Проверка постоянного решения", En_CaseState.TEST_CUST),
    WAIT_SOLUTION ("Ожидание постоянного решения", En_CaseState.OPENED),
    REJECT_WA ("Временное решение отклонено", En_CaseState.OPENED),
    CONFIRM_WA ("Временное решение принято", En_CaseState.TEST_CUST),
    CLOSED ("Закрыт", En_CaseState.VERIFIED);


    HpsmStatus (String code, En_CaseState caseState) {
        this.hpsmCode = code;
        this.caseState = caseState;
    }

    private final String hpsmCode;
    private final En_CaseState caseState;


    public String getHpsmCode() {
        return hpsmCode;
    }

    public En_CaseState getCaseState() {
        return caseState;
    }

    public static HpsmStatus parse (String code) {
        if (code == null || code.isEmpty())
            return null;

        for (HpsmStatus it : HpsmStatus.values())
            if (it.hpsmCode.equalsIgnoreCase(code))
                return it;

        return null;
    }
}
