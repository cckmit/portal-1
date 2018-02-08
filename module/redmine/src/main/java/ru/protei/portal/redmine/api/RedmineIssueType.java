package ru.protei.portal.redmine.api;

import ru.protei.portal.core.model.dict.En_CaseType;

import static ru.protei.portal.core.model.dict.En_CaseType.*;

public enum RedmineIssueType {
    ERROR ("Ошибка", En_CaseType.BUG),
    CRITICAL_ERROR("Критическая ошибка", En_CaseType.CRITICAL_BUG),
    CHANGE("Изменение", FREQ),
    SUPPORT("Поддержка", En_CaseType.SUPPORT),
    TESTING("Тестирование", En_CaseType.TEST),
    DOCUMENTATION("Документация", En_CaseType.DOCUMENTATION),
    CLIENT_REQUEST("Обращение клиента", En_CaseType.CLIENT_REQ),
    PROJECT_DOCUMENTATION("Проектная документация", En_CaseType.PROJECT_DOC),
    ANALYSIS("Анализ", En_CaseType.ANALYSIS),
    INFRASTRUCTURE("Инфраструктура", En_CaseType.INFRASTRUCTURE);

    RedmineIssueType (String type, En_CaseType caseType) {
        this.redmineIssueType = type;
        this.caseIssueType = caseType;
    }

    private String redmineIssueType;
    private En_CaseType caseIssueType;


    public String getRedmineIssueType() {
        return redmineIssueType;
    }

    public static RedmineIssueType find (En_CaseType type) {
        for (RedmineIssueType it : RedmineIssueType.values())
            if (it.caseIssueType == type)
                return it;

        return RedmineIssueType.ERROR;
    }

    public static RedmineIssueType find (String redmineIssueType) {
        for (RedmineIssueType it : RedmineIssueType.values())
            if (it.redmineIssueType.equals(redmineIssueType))
                return it;

        return RedmineIssueType.ERROR;
    }
}
