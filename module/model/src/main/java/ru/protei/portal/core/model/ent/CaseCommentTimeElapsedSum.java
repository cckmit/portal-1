package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.PermType;

import java.io.Serializable;
import java.util.Date;

@JdbcEntity(table = "case_comment", selectSql = "" +
        "case_comment.case_id case_id, case_comment.author_id author_id, " +
        "author.displayshortname author_display_name, " +
        "sum(case_comment.time_elapsed) time_elapsed_sum, " +
        "sum(IF(case_comment.time_elapsed_type IS NUll or case_comment.time_elapsed_type = 0, case_comment.time_elapsed, 0)) time_elapsed_none, " +
        "sum(IF(case_comment.time_elapsed_type = 1, case_comment.time_elapsed, 0)) time_elapsed_watch, " +
        "sum(IF(case_comment.time_elapsed_type = 2, case_comment.time_elapsed, 0)) time_elapsed_night_work, " +
        "sum(IF(case_comment.time_elapsed_type = 3, case_comment.time_elapsed, 0)) time_elapsed_SoftInstall, " +
        "sum(IF(case_comment.time_elapsed_type = 4, case_comment.time_elapsed, 0)) time_elapsed_SoftUpdate, " +
        "sum(IF(case_comment.time_elapsed_type = 5, case_comment.time_elapsed, 0)) time_elapsed_SoftConfig, " +
        "sum(IF(case_comment.time_elapsed_type = 6, case_comment.time_elapsed, 0)) time_elapsed_Testing, " +
        "sum(IF(case_comment.time_elapsed_type = 7, case_comment.time_elapsed, 0)) time_elapsed_Consultation, " +
        "sum(IF(case_comment.time_elapsed_type = 8, case_comment.time_elapsed, 0)) time_elapsed_Meeting, " +
        "sum(IF(case_comment.time_elapsed_type = 9, case_comment.time_elapsed, 0)) time_elapsed_DiscussionOfImprovements, " +
        "sum(IF(case_comment.time_elapsed_type = 10, case_comment.time_elapsed, 0)) time_elapsed_LogAnalysis, " +
        "sum(IF(case_comment.time_elapsed_type = 11, case_comment.time_elapsed, 0)) time_elapsed_SolveProblems, " +
        "case_object.caseno case_no, case_object.private_flag private_flag, case_object.case_name case_name, " +
        "company.cname case_company_name, manager.displayshortname manager_display_name, " +
        "case_object.importance importance, case_state.state state_name, importance_level.code code, case_object.created created, " +
        "product.UNIT_NAME product_name " +
        "from case_comment " +
        "left outer join person author on case_comment.author_id = author.id " +
        "left outer join case_object case_object on case_comment.case_id = case_object.id " +
        "left outer join importance_level importance_level on importance = importance_level.id " +
        "join case_state on case_object.STATE = case_state.id " +
        "left outer join company company on case_object.initiator_company = company.id " +
        "left outer join person manager on case_object.manager = manager.id " +
        "left outer join dev_unit product on case_object.product_id = product.id "
)
public class CaseCommentTimeElapsedSum implements Serializable {

    @JdbcColumn(name = "case_id", permType = PermType.READ_ONLY)
    private Long caseId;

    @JdbcColumn(name = "author_id", permType = PermType.READ_ONLY)
    private Long authorId;

    @JdbcColumn(name = "product_name", permType = PermType.READ_ONLY)
    private String productName;

    @JdbcColumn(name = "author_display_name", permType = PermType.READ_ONLY)
    private String authorDisplayName;

    @JdbcColumn(name = "time_elapsed_sum", permType = PermType.READ_ONLY)
    private Long timeElapsedSum;

    @JdbcColumn(name = "time_elapsed_none", permType = PermType.READ_ONLY)
    private Long timeElapsedNone;

    @JdbcColumn(name = "time_elapsed_watch", permType = PermType.READ_ONLY)
    private Long timeElapsedWatch;

    @JdbcColumn(name = "time_elapsed_night_work", permType = PermType.READ_ONLY)
    private Long timeElapsedNightWork;

    @JdbcColumn(name = "time_elapsed_SoftInstall", permType = PermType.READ_ONLY)
    private Long timeElapsedTypeSoftInstall;
    @JdbcColumn(name = "time_elapsed_SoftUpdate", permType = PermType.READ_ONLY)
    private Long timeElapsedTypeSoftUpdate;
    @JdbcColumn(name = "time_elapsed_SoftConfig", permType = PermType.READ_ONLY)
    private Long timeElapsedTypeSoftConfig;
    @JdbcColumn(name = "time_elapsed_Testing", permType = PermType.READ_ONLY)
    private Long timeElapsedTypeTesting;
    @JdbcColumn(name = "time_elapsed_Consultation", permType = PermType.READ_ONLY)
    private Long timeElapsedTypeConsultation;
    @JdbcColumn(name = "time_elapsed_Meeting", permType = PermType.READ_ONLY)
    private Long timeElapsedTypeMeeting;
    @JdbcColumn(name = "time_elapsed_DiscussionOfImprovements", permType = PermType.READ_ONLY)
    private Long timeElapsedTypeDiscussionOfImprovements;
    @JdbcColumn(name = "time_elapsed_LogAnalysis", permType = PermType.READ_ONLY)
    private Long timeElapsedTypeLogAnalysis;
    @JdbcColumn(name = "time_elapsed_SolveProblems", permType = PermType.READ_ONLY)
    private Long timeElapsedTypeSolveProblems;

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

    @JdbcColumn(name = "code", permType = PermType.READ_ONLY)
    private String importanceCode;

    @JdbcColumn(name = "state_name", permType = PermType.READ_ONLY)
    private String caseStateName;

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

    public Long getTimeElapsedNone() {
        return timeElapsedNone;
    }

    public Long getTimeElapsedWatch() {
        return timeElapsedWatch;
    }

    public Long getTimeElapsedNightWork() {
        return timeElapsedNightWork;
    }

    public Long getTimeElapsedTypeSoftInstall() {
        return timeElapsedTypeSoftInstall;
    }

    public Long getTimeElapsedTypeSoftUpdate() {
        return timeElapsedTypeSoftUpdate;
    }

    public Long getTimeElapsedTypeSoftConfig() {
        return timeElapsedTypeSoftConfig;
    }

    public Long getTimeElapsedTypeTesting() {
        return timeElapsedTypeTesting;
    }

    public Long getTimeElapsedTypeConsultation() {
        return timeElapsedTypeConsultation;
    }

    public Long getTimeElapsedTypeMeeting() {
        return timeElapsedTypeMeeting;
    }

    public Long getTimeElapsedTypeDiscussionOfImprovements() {
        return timeElapsedTypeDiscussionOfImprovements;
    }

    public Long getTimeElapsedTypeLogAnalysis() {
        return timeElapsedTypeLogAnalysis;
    }

    public Long getTimeElapsedTypeSolveProblems() {
        return timeElapsedTypeSolveProblems;
    }

    public String getProductName() {
        return productName;
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

    public String getCaseStateName() {
        return caseStateName;
    }

    public Date getCaseCreated() {
        return caseCreated;
    }

    public void setTimeElapsedSum(Long timeElapsedSum) {
        this.timeElapsedSum = timeElapsedSum;
    }

    public String getImportanceCode() {
        return importanceCode;
    }

    @Override
    public String toString() {
        return "CaseCommentTimeElapsedSum{" +
                "caseId=" + caseId +
                ", authorId=" + authorId +
                ", productName='" + productName + '\'' +
                ", authorDisplayName='" + authorDisplayName + '\'' +
                ", timeElapsedSum=" + timeElapsedSum +
                ", timeElapsedNone=" + timeElapsedNone +
                ", timeElapsedWatch=" + timeElapsedWatch +
                ", timeElapsedNightWork=" + timeElapsedNightWork +
                ", timeElapsedTypeSoftInstall=" + timeElapsedTypeSoftInstall +
                ", timeElapsedTypeSoftUpdate=" + timeElapsedTypeSoftUpdate +
                ", timeElapsedTypeSoftConfig=" + timeElapsedTypeSoftConfig +
                ", timeElapsedTypeTesting=" + timeElapsedTypeTesting +
                ", timeElapsedTypeConsultation=" + timeElapsedTypeConsultation +
                ", timeElapsedTypeMeeting=" + timeElapsedTypeMeeting +
                ", timeElapsedTypeDiscussionOfImprovements=" + timeElapsedTypeDiscussionOfImprovements +
                ", timeElapsedTypeLogAnalysis=" + timeElapsedTypeLogAnalysis +
                ", timeElapsedTypeSolveProblems=" + timeElapsedTypeSolveProblems +
                ", caseNumber=" + caseNumber +
                ", casePrivateCase=" + casePrivateCase +
                ", caseName='" + caseName + '\'' +
                ", caseCompanyName='" + caseCompanyName + '\'' +
                ", caseManagerDisplayName='" + caseManagerDisplayName + '\'' +
                ", caseImpLevel=" + caseImpLevel +
                ", caseStateName='" + caseStateName + '\'' +
                ", caseCreated=" + caseCreated +
                '}';
    }
}
