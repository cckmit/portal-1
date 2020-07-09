package ru.protei.portal.core.model.dict;

public enum En_CaseStateUsageInCompanies {
    NONE,
    ALL,
    SELECTED;

    public static En_CaseStateUsageInCompanies getButOrdinal(int ordinal) {
        for (En_CaseStateUsageInCompanies value: values()) {
            if(ordinal == value.ordinal())
                return value;
        }

        return null;
    }
}
