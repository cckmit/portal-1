package ru.protei.portal.redmine.api;

import ru.protei.portal.core.model.dict.En_ImportanceLevel;

public enum RedmineIssuePriority {
    LOW (3, En_ImportanceLevel.LOW),
    BASIC (4, En_ImportanceLevel.BASIC),
    HIGH (5, En_ImportanceLevel.IMPORTANT),
    IMPORTANT (6, En_ImportanceLevel.CRITICAL),
    CRITICAL(7, En_ImportanceLevel.IMMEDIATE);

    RedmineIssuePriority (int level, En_ImportanceLevel importanceLevel) {
        this.redminePriorityLevel = level;
        this.caseImpLevel = importanceLevel;
    }

    private int redminePriorityLevel;
    private En_ImportanceLevel caseImpLevel;

    public int getRedminePriorityLevel() {
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

    public static RedmineIssuePriority find (int hpsmLevel) {
        for (RedmineIssuePriority it : RedmineIssuePriority.values())
            if (it.redminePriorityLevel == hpsmLevel)
                return it;
        return BASIC;
    }
}
