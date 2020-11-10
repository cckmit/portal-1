package ru.protei.portal.core.model.dict;


import ru.protei.portal.core.model.util.CrmConstants;

public enum En_ExpiringProjectTSVPeriod {
    DAYS_7(7 * CrmConstants.Time.DAY),
    DAYS_14(14 * CrmConstants.Time.DAY),
    DAYS_30(30 * CrmConstants.Time.DAY);

    En_ExpiringProjectTSVPeriod(long time) {
        this.time = time;
    }

    private final long time;
    public long getTime() { return time; }
}
