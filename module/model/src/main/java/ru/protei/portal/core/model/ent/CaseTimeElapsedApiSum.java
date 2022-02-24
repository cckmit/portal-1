package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;

import static ru.protei.portal.core.model.ent.CaseTimeElapsedApiSum.Columns.CASE_ID;

@JdbcEntity(table = "case_comment")
public class CaseTimeElapsedApiSum {
    public static final String CASE_OBJECT_ALIAS = "CO";

    @JdbcColumn(name = "created")
    private Date date;

    @JdbcColumn(name = "time_elapsed")
    private Long elapsedTime;

    @JdbcColumn(name = "author_id")
    private Long personId;

    @JdbcColumn(name = CASE_ID)
    private Long issueId;

    @JdbcJoinedColumn(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
            table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS,
            mappedColumn = CaseObject.Columns.CASENO)
    private Long caseNumber;

    @JdbcJoinedColumn(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
            table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS,
            mappedColumn = CaseObject.Columns.IMPORTANCE)
    private Integer impLevel;

    @JdbcJoinedColumn(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
            table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS,
            mappedColumn = CaseObject.Columns.STATE)
    private long stateId;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
                    table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.STATE, remoteColumn = "id", table = "case_state")
    }, mappedColumn = "state")
    private String stateName;

    @JdbcJoinedColumn(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
            table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS,
            mappedColumn = CaseObject.Columns.INITIATOR_COMPANY)
    private Long initiatorCompanyId;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
                    table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.INITIATOR_COMPANY, remoteColumn = "id", table = "company")
    }, mappedColumn = "cname")
    private String initiatorCompanyName;

    @JdbcJoinedColumn(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
            table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS,
            mappedColumn = CaseObject.Columns.PRODUCT_ID)
    private Long productId;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
                    table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.PRODUCT_ID, remoteColumn = "id", table = "dev_unit")
    }, mappedColumn = "UNIT_NAME")
    private String productName;

    @JdbcJoinedColumn(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
            table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS,
            mappedColumn = CaseObject.Columns.MANAGER)
    private Long managerId;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
                    table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.MANAGER, remoteColumn = "id", table = "person")
    }, mappedColumn = "displayShortName")
    private String managerName;

    @JdbcJoinedColumn(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
            table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS,
            mappedColumn = CaseObject.Columns.MANAGER_COMPANY_ID)
    private Long managerCompanyId;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
                    table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.MANAGER_COMPANY_ID, remoteColumn = "id", table = "company")
    }, mappedColumn = "cname")
    private String managerCompanyName;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(Long caseNumber) {
        this.caseNumber = caseNumber;
    }

    public Integer getImpLevel() {
        return impLevel;
    }

    public void setImpLevel(Integer impLevel) {
        this.impLevel = impLevel;
    }

    public long getStateId() {
        return stateId;
    }

    public void setStateId(long stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public Long getInitiatorCompanyId() {
        return initiatorCompanyId;
    }

    public void setInitiatorCompanyId(Long initiatorCompanyId) {
        this.initiatorCompanyId = initiatorCompanyId;
    }

    public String getInitiatorCompanyName() {
        return initiatorCompanyName;
    }

    public void setInitiatorCompanyName(String initiatorCompanyName) {
        this.initiatorCompanyName = initiatorCompanyName;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public Long getManagerCompanyId() {
        return managerCompanyId;
    }

    public void setManagerCompanyId(Long managerCompanyId) {
        this.managerCompanyId = managerCompanyId;
    }

    public String getManagerCompanyName() {
        return managerCompanyName;
    }

    public void setManagerCompanyName(String managerCompanyName) {
        this.managerCompanyName = managerCompanyName;
    }

    public interface Columns {
        String CASE_ID = "case_id";
    }

    @Override
    public String toString() {
        return "CaseTimeElapsedApiSum{" +
                "date=" + date +
                ", elapsedTime=" + elapsedTime +
                ", personId=" + personId +
                ", issueId=" + issueId +
                ", issueNumber='" + caseNumber + '\'' +
                ", impLevel=" + impLevel +
                ", stateId=" + stateId +
                ", stateName='" + stateName + '\'' +
                ", initiatorCompanyId=" + initiatorCompanyId +
                ", initiatorCompanyName='" + initiatorCompanyName + '\'' +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", managerId=" + managerId +
                ", managerName='" + managerName + '\'' +
                ", managerCompanyId=" + managerCompanyId +
                ", managerCompanyName='" + managerCompanyName + '\'' +
                '}';
    }
}
