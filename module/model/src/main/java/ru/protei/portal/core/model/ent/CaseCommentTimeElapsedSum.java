package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;

@JdbcEntity(table = "case_comment", selectSql = "" +
        "case_comment.case_id case_id, case_comment.author_id author_id, " +
        "author.displayshortname author_display_name, sum(case_comment.time_elapsed) time_elapsed_sum, " +
        "case_object.caseno case_no, case_object.private_flag private_flag, case_object.case_name case_name, " +
        "company.cname case_company_name, manager.displayshortname manager_display_name, " +
        "case_object.importance importance, case_object.state state, case_object.created created " +
        "from case_comment " +
        "left outer join Person author on case_comment.author_id = author.id " +
        "left outer join case_object case_object on case_comment.case_id = case_object.id " +
        "left outer join company company on case_object.initiator_company = company.id " +
        "left outer join Person manager on case_object.manager = manager.id "
)
public class CaseCommentTimeElapsedSum implements Serializable {

    @JdbcColumn(name = "case_id", permType = PermType.READ_ONLY)
    private Long caseId;

    @JdbcColumn(name = "author_id", permType = PermType.READ_ONLY)
    private Long authorId;

    @JdbcColumn(name = "author_display_name", permType = PermType.READ_ONLY)
    private String authorDisplayName;

    @JdbcColumn(name = "time_elapsed_sum", permType = PermType.READ_ONLY)
    private Long timeElapsedSum;

    @JdbcColumn(name = "case_no", permType = PermType.READ_ONLY)
    private Long caseNumber;

    @JdbcColumn(name = "private_flag", permType = PermType.READ_ONLY)
    private boolean casePrivateCase;

    @JdbcColumn(name = "case_name", permType = PermType.READ_ONLY)
    private String caseName;

    @JdbcColumn(name = "case_company_name", permType = PermType.READ_ONLY)
    private String caseCompanyName;

    @JdbcColumn(name = "manager_display_name", permType = PermType.READ_ONLY)
    private String caseManagerDisplayName;

    @JdbcColumn(name = "importance", permType = PermType.READ_ONLY)
    private Integer caseImpLevel;

    @JdbcColumn(name = "state", permType = PermType.READ_ONLY)
    private long caseStateId;

    @JdbcColumn(name = "created", permType = PermType.READ_ONLY)
    private Date caseCreated;

    public Long getCaseId() {
        return caseId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public Long getTimeElapsedSum() {
        return timeElapsedSum;
    }

    public Long getCaseNumber() {
        return caseNumber;
    }

    public boolean isCasePrivateCase() {
        return casePrivateCase;
    }

    public String getCaseName() {
        return caseName;
    }

    public String getCaseCompanyName() {
        return caseCompanyName;
    }

    public String getCaseManagerDisplayName() {
        return caseManagerDisplayName;
    }

    public Integer getCaseImpLevel() {
        return caseImpLevel;
    }

    public long getCaseStateId() {
        return caseStateId;
    }

    public Date getCaseCreated() {
        return caseCreated;
    }

    public En_CaseState getCaseState() {
        return En_CaseState.getById(caseStateId);
    }

    public void setTimeElapsedSum(Long timeElapsedSum) {
        this.timeElapsedSum = timeElapsedSum;
    }

    @Override
    public String toString() {
        return "CaseCommentTimeElapsedSum{" +
                "caseId=" + caseId +
                ", authorId=" + authorId +
                ", authorDisplayName='" + authorDisplayName + '\'' +
                ", timeElapsedSum=" + timeElapsedSum +
                ", caseNumber=" + caseNumber +
                ", casePrivateCase=" + casePrivateCase +
                ", caseName='" + caseName + '\'' +
                ", caseCompanyName='" + caseCompanyName + '\'' +
                ", caseManagerDisplayName='" + caseManagerDisplayName + '\'' +
                ", caseImpLevel=" + caseImpLevel +
                ", caseStateId=" + caseStateId +
                ", caseCreated=" + caseCreated +
                '}';
    }
}
