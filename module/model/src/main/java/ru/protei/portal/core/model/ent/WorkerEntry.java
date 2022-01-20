package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;

import static ru.protei.portal.core.model.ent.WorkerEntry.Columns.*;

/**
 * Created by michael on 17.05.16.
 */
@JdbcEntity(table = "worker_entry")
public class WorkerEntry extends AuditableObject {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="created")
    private Date created;

    @JdbcColumn(name="personId")
    private Long personId;

    @JdbcColumn(name=POSITION_DEPARTMENT_ID)
    private Long departmentId;

    @JdbcJoinedColumn(localColumn = "dep_id", table = "company_dep", sqlTableAlias = "d", remoteColumn = "id", mappedColumn = "dep_name")
    private String departmentName;

    @JdbcJoinedColumn(localColumn = "dep_id", table = "company_dep", sqlTableAlias = "d", remoteColumn = "id", mappedColumn = "dep_extId")
    private String departmentExternalId;

    @JdbcColumn(name="companyId")
    private Long companyId;

    @JdbcJoinedColumn(localColumn = "companyId", table = "company_group_home", remoteColumn = "companyId", mappedColumn = "external_code")
    private String externalCode;

    @JdbcJoinedColumn(localColumn = "companyId", table = "company", remoteColumn = "id", mappedColumn = "cname")
    private String companyName;

    @JdbcColumn(name="positionId")
    private Long positionId;

    @JdbcJoinedColumn(localColumn = POSITION_NAME, table = "worker_position", remoteColumn = "id", mappedColumn = "pos_name")
    private String positionName;

    @JdbcColumn(name="hireDate")
    private Date hireDate;

    @JdbcColumn(name="hireOrderNo")
    private String hireOrderNo;

    @JdbcColumn(name="active")
    private int activeFlag;

    @JdbcColumn(name = "worker_extId")
    private String externalId;

    @JdbcColumn(name = "is_contract_agreement")
    private boolean isContractAgreement;

    @JdbcColumn(name = Columns.FIRED_FATE)
    private Date firedDate;

    @JdbcColumn(name = "is_deleted")
    private Boolean isDeleted;

    @JdbcColumn(name = Columns.NEW_POSITION_NAME)
    private String newPositionName;

    @JdbcColumn(name = Columns.NEW_POSITION_DEPARTMENT_ID)
    private Long newPositionDepartmentId;

    @JdbcColumn(name = Columns.NEW_POSITION_TRANSFER_DATE)
    private Date newPositionTransferDate;

    @Override
    public String getAuditType() {
        return "Worker";
    }


    public WorkerEntry () {
        this.activeFlag = 1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName( String departmentName ) {
        this.departmentName = departmentName;
    }

    public String getDepartmentExternalId() {
        return departmentExternalId;
    }

    public void setDepartmentExternalId( String departmentExternalId ) {
        this.departmentExternalId = departmentExternalId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getExternalCode() {
        return externalCode;
    }

    public void setExternalCode(String externalCode) {
        this.externalCode = externalCode;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName( String positionName ) {
        this.positionName = positionName;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public String getHireOrderNo() {
        return hireOrderNo;
    }

    public void setHireOrderNo(String hireOrderNo) {
        this.hireOrderNo = hireOrderNo;
    }

    public int getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(int activeFlag) {
        this.activeFlag = activeFlag;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName( String companyName ) {
        this.companyName = companyName;
    }

    public boolean isMain() {
        return activeFlag > 0;
    }

    public boolean getContractAgreement() {
        return isContractAgreement;
    }

    public void setContractAgreement(boolean contractAgreement) {
        isContractAgreement = contractAgreement;
    }

    public Date getFiredDate() {
        return firedDate;
    }

    public void setFiredDate(Date firedDate) {
        this.firedDate = firedDate;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String getNewPositionName() {
        return newPositionName;
    }

    public void setNewPositionName(String newPositionName) {
        this.newPositionName = newPositionName;
    }

    public Long getNewPositionDepartmentId() {
        return newPositionDepartmentId;
    }

    public void setNewPositionDepartmentId(Long newPositionDepartmentId) {
        this.newPositionDepartmentId = newPositionDepartmentId;
    }

    public Date getNewPositionTransferDate() {
        return newPositionTransferDate;
    }

    public void setNewPositionTransferDate(Date newPositionTransferDate) {
        this.newPositionTransferDate = newPositionTransferDate;
    }

    public interface Columns {
        String FIRED_FATE = "fired_date";
        String POSITION_NAME = "positionId";
        String POSITION_DEPARTMENT_ID = "dep_id";
        String NEW_POSITION_NAME = "new_position_name";
        String NEW_POSITION_DEPARTMENT_ID = "new_position_department_id";
        String NEW_POSITION_TRANSFER_DATE = "new_position_transfer_date";
    }
}
