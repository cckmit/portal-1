package ru.protei.portal.hpsm;

import ru.protei.portal.core.model.dict.En_ImportanceLevel;

/**
 * Created by michael on 24.04.17.
 */
public enum HpsmSeverity {

    LEVEL1 (1, En_ImportanceLevel.CRITICAL),
    LEVEL2 (2, En_ImportanceLevel.IMPORTANT),
    LEVEL3 (3, En_ImportanceLevel.BASIC),
    LEVEL4 (4, En_ImportanceLevel.COSMETIC);

    HpsmSeverity (int level, En_ImportanceLevel importanceLevel) {
        this.hpsmLevel = level;
        this.caseImpLevel = importanceLevel;
    }

    private int hpsmLevel;
    private En_ImportanceLevel caseImpLevel;


    public int getHpsmLevel() {
        return hpsmLevel;
    }

    public En_ImportanceLevel getCaseImpLevel() {
        return caseImpLevel;
    }

    public static HpsmSeverity find (int hpsmLevel) {
        for (HpsmSeverity it : HpsmSeverity.values())
            if (it.hpsmLevel == hpsmLevel)
                return it;

        return HpsmSeverity.LEVEL3;
    }
}
