package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;

import static ru.protei.portal.core.model.ent.CaseElapsedTimeApi.Columns.CASE_ID;

@JdbcEntity(table = "case_comment")
public class CaseElapsedTimeApi {
    public static final String CASE_OBJECT_ALIAS = "CO";

    @JdbcColumn(name = "id")
    private Long id;

    @JdbcColumn(name = "created")
    private Date date;

    @JdbcColumn(name = "time_elapsed")
    private Long elapsedTime;

    @JdbcColumn(name="time_elapsed_type")
    @JdbcEnumerated(EnumType.ID)
    private En_TimeElapsedType timeElapsedType;

    @JdbcColumn(name = "author_id")
    private Long authorId;

    @JdbcColumn(name = CASE_ID)
    private Long caseId;

    @JdbcJoinedColumn(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
            table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS,
            mappedColumn = CaseObject.Columns.CASENO)
    private Long caseNumber;

    @JdbcJoinedColumn(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
            table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS,
            mappedColumn = CaseObject.Columns.IMPORTANCE)
    private Integer caseImpLevel;

    @JdbcJoinedColumn(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
            table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS,
            mappedColumn = CaseObject.Columns.STATE)
    private long caseStateId;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
                    table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.STATE, remoteColumn = "id", table = "case_state")
    }, mappedColumn = "state")
    private String caseStateName;

    @JdbcJoinedColumn(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
            table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS,
            mappedColumn = CaseObject.Columns.INITIATOR_COMPANY)
    private Long caseInitiatorCompanyId;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
                    table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.INITIATOR_COMPANY, remoteColumn = "id", table = "company")
    }, mappedColumn = "cname")
    private String caseInitiatorCompanyName;

    @JdbcJoinedColumn(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
            table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS,
            mappedColumn = CaseObject.Columns.PRODUCT_ID)
    private Long caseProductId;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
                    table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.PRODUCT_ID, remoteColumn = "id", table = "dev_unit")
    }, mappedColumn = "UNIT_NAME")
    private String caseProductName;

    @JdbcJoinedColumn(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
            table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS,
            mappedColumn = CaseObject.Columns.MANAGER)
    private Long caseManagerId;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
                    table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.MANAGER, remoteColumn = "id", table = "person")
    }, mappedColumn = "displayShortName")
    private String caseManagerName;

    @JdbcJoinedColumn(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
            table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS,
            mappedColumn = CaseObject.Columns.MANAGER_COMPANY_ID)
    private Long caseManagerCompanyId;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = CASE_ID, remoteColumn = CaseObject.Columns.ID,
                    table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.MANAGER_COMPANY_ID, remoteColumn = "id", table = "company")
    }, mappedColumn = "cname")
    private String caseManagerCompanyName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public En_TimeElapsedType getTimeElapsedType() {
        return timeElapsedType;
    }

    public void setTimeElapsedType(En_TimeElapsedType timeElapsedType) {
        this.timeElapsedType = timeElapsedType;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public Long getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(Long caseNumber) {
        this.caseNumber = caseNumber;
    }

    public Integer getCaseImpLevel() {
        return caseImpLevel;
    }

    public void setCaseImpLevel(Integer caseImpLevel) {
        this.caseImpLevel = caseImpLevel;
    }

    public long getCaseStateId() {
        return caseStateId;
    }

    public void setCaseStateId(long caseStateId) {
        this.caseStateId = caseStateId;
    }

    public String getCaseStateName() {
        return caseStateName;
    }

    public void setCaseStateName(String caseStateName) {
        this.caseStateName = caseStateName;
    }

    public Long getCaseInitiatorCompanyId() {
        return caseInitiatorCompanyId;
    }

    public void setCaseInitiatorCompanyId(Long caseInitiatorCompanyId) {
        this.caseInitiatorCompanyId = caseInitiatorCompanyId;
    }

    public String getCaseInitiatorCompanyName() {
        return caseInitiatorCompanyName;
    }

    public void setCaseInitiatorCompanyName(String caseInitiatorCompanyName) {
        this.caseInitiatorCompanyName = caseInitiatorCompanyName;
    }

    public Long getCaseProductId() {
        return caseProductId;
    }

    public void setCaseProductId(Long caseProductId) {
        this.caseProductId = caseProductId;
    }

    public String getCaseProductName() {
        return caseProductName;
    }

    public void setCaseProductName(String caseProductName) {
        this.caseProductName = caseProductName;
    }

    public Long getCaseManagerId() {
        return caseManagerId;
    }

    public void setCaseManagerId(Long caseManagerId) {
        this.caseManagerId = caseManagerId;
    }

    public String getCaseManagerName() {
        return caseManagerName;
    }

    public void setCaseManagerName(String caseManagerName) {
        this.caseManagerName = caseManagerName;
    }

    public Long getCaseManagerCompanyId() {
        return caseManagerCompanyId;
    }

    public void setCaseManagerCompanyId(Long caseManagerCompanyId) {
        this.caseManagerCompanyId = caseManagerCompanyId;
    }

    public String getCaseManagerCompanyName() {
        return caseManagerCompanyName;
    }

    public void setCaseManagerCompanyName(String caseManagerCompanyName) {
        this.caseManagerCompanyName = caseManagerCompanyName;
    }

    public interface Columns {
        String CASE_ID = "case_id";
    }

    @Override
    public String toString() {
        return "CaseElapsedTimeApi{" +
                "id=" + id +
                ", date=" + date +
                ", elapsedTime=" + elapsedTime +
                ", authorId=" + authorId +
                ", caseId=" + caseId +
                ", caseNumber=" + caseNumber +
                ", impLevel=" + caseImpLevel +
                ", stateId=" + caseStateId +
                ", stateName='" + caseStateName + '\'' +
                ", initiatorCompanyId=" + caseInitiatorCompanyId +
                ", initiatorCompanyName='" + caseInitiatorCompanyName + '\'' +
                ", productId=" + caseProductId +
                ", productName='" + caseProductName + '\'' +
                ", managerId=" + caseManagerId +
                ", managerName='" + caseManagerName + '\'' +
                ", managerCompanyId=" + caseManagerCompanyId +
                ", managerCompanyName='" + caseManagerCompanyName + '\'' +
                '}';
    }
}
