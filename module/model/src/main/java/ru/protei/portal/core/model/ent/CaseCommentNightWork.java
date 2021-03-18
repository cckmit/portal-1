package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.PermType;

import java.io.Serializable;
import java.util.Date;

@JdbcEntity(selectSql = "" +
        "(CASE " +
        "            WHEN HOUR(cc.CREATED) < 23 " +
        "                THEN date(cc.CREATED) " +
        "            ELSE ADDDATE(date(cc.CREATED), 1) " +
        "    END) day, sum(cc.time_elapsed) nightWorkTimeElapsedSum, count(cc.time_elapsed) nightWorkTimeElapsedCount, " +
        "       p.displayname author_display_name, co.CASENO case_no, c.cname case_company_name, " +
        "       cust.displayname initiator_display_name, du.UNIT_NAME product_name, max(cc.ID) last_comment_id " +
        "FROM case_object co " +
        "         join case_comment cc on cc.CASE_ID = co.ID " +
        "         join person p on cc.AUTHOR_ID = p.id " +
        "         join person cust on co.INITIATOR = cust.id " +
        "         join company c on co.initiator_company = c.id " +
        "         join dev_unit du on co.product_id = du.ID"
)
public class CaseCommentNightWork implements Serializable {
    @JdbcColumn(name = "day", permType = PermType.READ_ONLY)
    private Date day;

    @JdbcColumn(name = "nightWorkTimeElapsedSum", permType = PermType.READ_ONLY)
    private Long nightWorkTimeElapsedSum;

    @JdbcColumn(name = "nightWorkTimeElapsedCount", permType = PermType.READ_ONLY)
    private Long nightWorkTimeElapsedCount;

    @JdbcColumn(name = "author_display_name", permType = PermType.READ_ONLY)
    private String authorDisplayName;

    @JdbcColumn(name = "case_no", permType = PermType.READ_ONLY)
    private Long caseNumber;

    @JdbcColumn(name = "case_company_name", permType = PermType.READ_ONLY)
    private String caseCompanyName;

    @JdbcColumn(name = "initiator_display_name", permType = PermType.READ_ONLY)
    private String initiatorDisplayName;

    @JdbcColumn(name = "product_name", permType = PermType.READ_ONLY)
    private String productName;

    @JdbcColumn(name = "last_comment_id", permType = PermType.READ_ONLY)
    private Long lastCommentId;

    // not db column
    private CaseComment lastCaseComment;

    public Date getDay() {
        return day;
    }

    public Long getNightWorkTimeElapsedSum() {
        return nightWorkTimeElapsedSum;
    }

    public Long getNightWorkTimeElapsedCount() {
        return nightWorkTimeElapsedCount;
    }

    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public Long getCaseNumber() {
        return caseNumber;
    }

    public String getCaseCompanyName() {
        return caseCompanyName;
    }

    public String getInitiatorDisplayName() {
        return initiatorDisplayName;
    }

    public String getProductName() {
        return productName;
    }

    public Long getLastCommentId() {
        return lastCommentId;
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
                ", nightWorkTimeElapsedSum=" + nightWorkTimeElapsedSum +
                ", nightWorkTimeElapsedCount=" + nightWorkTimeElapsedCount +
                ", authorDisplayName='" + authorDisplayName + '\'' +
                ", caseNumber=" + caseNumber +
                ", caseCompanyName='" + caseCompanyName + '\'' +
                ", initiatorDisplayName='" + initiatorDisplayName + '\'' +
                ", productName='" + productName + '\'' +
                ", lastCommentId=" + lastCommentId +
                '}';
    }
}
