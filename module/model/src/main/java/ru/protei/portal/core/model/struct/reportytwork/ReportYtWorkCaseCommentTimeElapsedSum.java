package ru.protei.portal.core.model.struct.reportytwork;

import java.io.Serializable;

public class ReportYtWorkCaseCommentTimeElapsedSum implements Serializable {
    private Long personId;
    
    // потраченное время
    private Long spentTime;

    // суррогатный id платформы - если surrogateInitiatorCompanyId = 1, то null
    private Long surrogatePlatformId;

    public ReportYtWorkCaseCommentTimeElapsedSum() {
    }

    public ReportYtWorkCaseCommentTimeElapsedSum(Long personId, Long spentTime, Long surrogatePlatformId) {
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
        return "ReportYtWorkCaseCommentTimeElapsedSum{" +
                "personId=" + personId +
                ", spentTime=" + spentTime +
                ", surrogatePlatformId=" + surrogatePlatformId +
                '}';
    }
}
