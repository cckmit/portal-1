package ru.protei.portal.redmine.api;

import ru.protei.portal.core.model.dict.En_CaseType;

import static ru.protei.portal.core.model.dict.En_CaseType.*;

public enum RedmineIssueType {
    /* @review
     *
     * Такой подход категорически неверен, нужно было спросить по поводу CaseType.
     * Мы всегда должны создавать только Case с типом CRM_SUPPORT, нам тип задачи
     * в Redmine абсолютно не важен, пусть он хоть какой будет, всегда отображается
     * в CRM_SUPPORT и только в него.
     * Типы BUG,FREQ, TASK - были введены для миграции старых данных текущего портала, где
     * есть такие сущности. Суть проблемы в том, что сейчас сделана поддержка работы
     * ТОЛЬКО с CRM_SUPPORT, никакие другие типы Case в новом портале не используются,
     * соответственно данные с такими типами просто не будут отображаться.
     * Этот enum нужно убрать и сделать обработку без него (нет смысла, ты всегда создаешь
     * только CRM_SUPPORT)
     *
     */
    ERROR (1, En_CaseType.BUG),
    CRITICAL_ERROR(10, En_CaseType.BUG),
    CHANGE(2, FREQ),
    SUPPORT(3, En_CaseType.TASK),
    TESTING(4, En_CaseType.TASK),
    DOCUMENTATION(5, En_CaseType.CRM_SUPPORT),
    CLIENT_REQUEST(7, En_CaseType.FREQ),
    PROJECT_DOCUMENTATION(8, En_CaseType.CRM_SUPPORT),
    ANALYSIS(9, En_CaseType.TASK),
    INFRASTRUCTURE(11, En_CaseType.PROJECT);

    RedmineIssueType (int typeId, En_CaseType caseType) {
        this.redmineIssueTypeId = typeId;
        this.caseIssueType = caseType;
    }

    private int redmineIssueTypeId;
    private En_CaseType caseIssueType;


    public int getRedmineIssueTypeId() {
        return redmineIssueTypeId;
    }

    public En_CaseType getCaseIssueType() {
        return caseIssueType;
    }

    public static RedmineIssueType find (En_CaseType type) {
        for (RedmineIssueType it : RedmineIssueType.values())
            if (it.caseIssueType == type)
                return it;
        return RedmineIssueType.ERROR;
    }

    public static En_CaseType find (int redmineIssueTypeId) {
        for (RedmineIssueType it : RedmineIssueType.values())
            if (it.redmineIssueTypeId == redmineIssueTypeId)
                return it.caseIssueType;

        return RedmineIssueType.ERROR.caseIssueType;
    }
}
