package ru.protei.portal.core.model.dict;

// Project.technicalSupportValidity
public enum En_ExpiringProjectTSVPeriod {
    DAYS_7(7),
    DAYS_14(14),
    DAYS_30(30);

    En_ExpiringProjectTSVPeriod(int days) {
        this.days = days;
    }

    private final int days;
    public int getDays() { return days; }
}
