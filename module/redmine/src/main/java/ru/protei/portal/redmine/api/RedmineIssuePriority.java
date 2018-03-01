package ru.protei.portal.redmine.api;

import ru.protei.portal.core.model.dict.En_ImportanceLevel;

public enum RedmineIssuePriority {
    /*
      @review Enum такой может и нужен, вот только маппинг его в наши приоритеты нужно было делать иначе.
      В любом случае, IMPORTANT -> IMPORTANT, не очень понятно, почему HIGH->IMPORTANT, а IMPORTANT->CRITICAL,
      оба случая, т.е. и HIGH, и IMPORTANT соответствуют нашему IMPORTANT

      Не забывай, также, что у заказчика возможно совсем другой список, я пока не очень представляю, как мы его будем
      отображать в наш и тем более, в обратном направлении.
      Но это могут быть замечания после запуска в работу.
     */

    LOW (3, En_ImportanceLevel.COSMETIC),
    BASIC (4, En_ImportanceLevel.BASIC),
    HIGH (5, En_ImportanceLevel.IMPORTANT),
    IMPORTANT (6, En_ImportanceLevel.CRITICAL),
    CRITICAL(7, En_ImportanceLevel.CRITICAL);

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

    public static RedmineIssuePriority find (int issueLvl) {
        for (RedmineIssuePriority it : RedmineIssuePriority.values())
            if (it.redminePriorityLevel == issueLvl)
                return it;
        return BASIC;
    }
}
