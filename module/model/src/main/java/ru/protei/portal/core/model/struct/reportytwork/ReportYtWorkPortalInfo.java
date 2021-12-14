package ru.protei.portal.core.model.struct.reportytwork;

import java.io.Serializable;

public class ReportYtWorkPortalInfo implements Serializable {
    private Long personId;
    
    // потраченное время
    private Long spentTime;

    // суррогатный id платформы - если домашняя компания или заказчик без платформы - то null
    private Long surrogatePlatformId;

    public ReportYtWorkPortalInfo() {
    }

    public ReportYtWorkPortalInfo(Long personId, Long spentTime, Long surrogatePlatformId) {
        this.personId = personId;
        this.spentTime = spentTime;
        this.surrogatePlatformId = surrogatePlatformId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getSpentTime() {
        return spentTime;
    }

    public void setSpentTime(Long spentTime) {
        this.spentTime = spentTime;
    }

    public Long getSurrogatePlatformId() {
        return surrogatePlatformId;
    }

    public void setSurrogatePlatformId(Long surrogatePlatformId) {
        this.surrogatePlatformId = surrogatePlatformId;
    }

    @Override
    public String toString() {
        return "ReportYtWorkPortalInfo{" +
                "personId=" + personId +
                ", spentTime=" + spentTime +
                ", surrogatePlatformId=" + surrogatePlatformId +
                '}';
    }
}
