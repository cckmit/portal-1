package ru.protei.portal.core.model.struct.reportytwork;

import java.io.Serializable;

public class ReportYtWorkCaseCommentTimeElapsedSum implements Serializable {
    private Long personId;
    
    // потраченное время
    private Integer spentTime;

    // суррогатный id платформы - если surrogateInitiatorCompanyId = 1, то null
    private Long surrogatePlatformId;

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Integer getSpentTime() {
        return spentTime;
    }

    public void setSpentTime(Integer spentTime) {
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
