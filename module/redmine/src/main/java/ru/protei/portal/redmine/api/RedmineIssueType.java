package ru.protei.portal.redmine.api;

import ru.protei.portal.core.model.dict.En_CaseType;

import static ru.protei.portal.core.model.dict.En_CaseType.*;

public enum RedmineIssueType {
    ERROR ("Ошибка", En_CaseType.BUG),
    CRITICAL_ERROR("Критическая ошибка", En_CaseType.BUG),
    CHANGE("Изменение", FREQ),
    SUPPORT("Поддержка", En_CaseType.TASK),
    TESTING("Тестирование", En_CaseType.TASK),
    DOCUMENTATION("Документация", En_CaseType.CRM_SUPPORT),
    CLIENT_REQUEST("Обращение клиента", En_CaseType.FREQ),
    PROJECT_DOCUMENTATION("Проектная документация", En_CaseType.CRM_SUPPORT),
    ANALYSIS("Анализ", En_CaseType.TASK),
    INFRASTRUCTURE("Инфраструктура", En_CaseType.PROJECT);

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
