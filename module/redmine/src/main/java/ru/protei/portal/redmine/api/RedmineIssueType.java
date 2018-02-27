package ru.protei.portal.redmine.api;

import ru.protei.portal.core.model.dict.En_CaseType;

import static ru.protei.portal.core.model.dict.En_CaseType.*;

public enum RedmineIssueType {
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
