package ru.protei.portal.core.model.ent;

import java.io.Serializable;
import java.util.Date;

public class CaseCommentNightWork implements Serializable {

    private Date day;

    private Long timeElapsedSum;

    private Long timeElapsedCount;

    private String authorDisplayName;

    private Long caseNumber;

    private String caseCompanyName;

    private String initiatorDisplayName;

    private String productName;

    private Long lastCommentId;

    private CaseComment lastCaseComment;

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public Long getTimeElapsedSum() {
        return timeElapsedSum;
    }

    public void setTimeElapsedSum(Long timeElapsedSum) {
        this.timeElapsedSum = timeElapsedSum;
    }

    public Long getTimeElapsedCount() {
        return timeElapsedCount;
    }

    public void setTimeElapsedCount(Long timeElapsedCount) {
        this.timeElapsedCount = timeElapsedCount;
    }

    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public void setAuthorDisplayName(String authorDisplayName) {
        this.authorDisplayName = authorDisplayName;
    }

    public Long getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(Long caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getCaseCompanyName() {
        return caseCompanyName;
    }

    public void setCaseCompanyName(String caseCompanyName) {
        this.caseCompanyName = caseCompanyName;
    }

    public String getInitiatorDisplayName() {
        return initiatorDisplayName;
    }

    public void setInitiatorDisplayName(String initiatorDisplayName) {
        this.initiatorDisplayName = initiatorDisplayName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getLastCommentId() {
        return lastCommentId;
    }

    public void setLastCommentId(Long lastCommentId) {
        this.lastCommentId = lastCommentId;
    }

    public CaseComment getLastCaseComment() {
        return lastCaseComment;
    }

    public void setLastCaseComment(CaseComment lastCaseComment) {
        this.lastCaseComment = lastCaseComment;
    }

    @Override
    public String toString() {
        return "CaseCommentNightWork{" +
                "day=" + day +
                ", timeElapsedSum=" + timeElapsedSum +
                ", timeElapsedCount=" + timeElapsedCount +
                ", authorDisplayName='" + authorDisplayName + '\'' +
                ", caseNumber=" + caseNumber +
                ", caseCompanyName='" + caseCompanyName + '\'' +
                ", initiatorDisplayName='" + initiatorDisplayName + '\'' +
                ", productName='" + productName + '\'' +
                ", lastCommentId=" + lastCommentId +
                ", lastCaseComment=" + lastCaseComment +
                '}';
    }

    public interface Columns {
        String DAY = "day";
        String TIME_ELAPSED_SUM = "timeElapsedSum";
        String TIME_ELAPSED_COUNT = "timeElapsedCount";
        String AUTHOR_DISPLAY_NAME = "authorDisplayName";
        String CASE_NUMBER = "caseNumber";
        String CASE_COMPANY_NAME = "caseCompanyName";
        String INITIATOR_DISPLAY_NAME = "initiatorDisplayName";
        String PRODUCT_NAME = "productName";
        String LAST_COMMENT_ID = "lastCommentId";
    }
}
