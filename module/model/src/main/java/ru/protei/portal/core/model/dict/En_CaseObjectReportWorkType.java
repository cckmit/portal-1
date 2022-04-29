package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_CaseObjectReportWorkType implements HasId {

    TYPE(1),
    DEPARTMENT(2),
    AUTHOR(3);

    En_CaseObjectReportWorkType(int id) {
        this.id = id;
    }

    private final int id;

    public int getId() {
        return id;
    }

    public static En_CaseObjectReportWorkType find (int id) {
        for (En_CaseObjectReportWorkType value : En_CaseObjectReportWorkType.values())
            if (value.id == id)
                return value;

        return null;
    }
}
