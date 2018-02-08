package ru.protei.portal.redmine.api;

import ru.protei.portal.core.model.dict.En_ImportanceLevel;

public enum RedmineIssuePriority {
    LOW ("Низкий", En_ImportanceLevel.LOW),
    BASIC ("Стандартный", En_ImportanceLevel.BASIC),
    HIGH ("Высокий", En_ImportanceLevel.IMPORTANT),
    IMPORTANT ("Срочный", En_ImportanceLevel.CRITICAL),
    CRITICAL("Немедленный", En_ImportanceLevel.IMMEDIATE);

    RedmineIssuePriority (String level, En_ImportanceLevel importanceLevel) {
        this.redminePriorityLevel = level;
        this.caseImpLevel = importanceLevel;
    }

    private String redminePriorityLevel;
    private En_ImportanceLevel caseImpLevel;

    public String getRedminePriorityLevel() {
        return redminePriorityLevel;
    }

    public En_ImportanceLevel getCaseImpLevel() {
        return caseImpLevel;
    }

    public static RedmineIssuePriority find (En_ImportanceLevel importanceLevel) {
        for (RedmineIssuePriority it : RedmineIssuePriority.values())
            if (it.caseImpLevel == importanceLevel)
                return it;

        return BASIC;
    }

    public static RedmineIssuePriority find (String hpsmLevel) {
        for (RedmineIssuePriority it : RedmineIssuePriority.values())
            if (it.redminePriorityLevel.equals(hpsmLevel))
                return it;

        return BASIC;
    }
}
