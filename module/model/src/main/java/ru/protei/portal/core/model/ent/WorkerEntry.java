package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;

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

    @JdbcJoinedObject(localColumn = "personId", remoteColumn = "id")
    private Person person;

    @JdbcColumn(name="dep_id")
    private Long departmentId;

    @JdbcJoinedObject(localColumn = "dep_id", remoteColumn = "id")
    private CompanyDepartment department;

    @JdbcColumn(name="companyId")
    private Long companyId;

    @JdbcJoinedColumn(localColumn = "companyId", table = "company_group_home", remoteColumn = "companyId", mappedColumn = "external_code")
    private String externalCode;

    @JdbcJoinedColumn(localColumn = "companyId", table = "company", remoteColumn = "id", mappedColumn = "cname")
    private String companyName;

    @JdbcColumn(name="positionId")
    private Long positionId;

    @JdbcJoinedObject(localColumn = "positionId", remoteColumn = "id")
    private WorkerPosition position;

    @JdbcColumn(name="hireDate")
    private Date hireDate;

    @JdbcColumn(name="hireOrderNo")
    private String hireOrderNo;

    @JdbcColumn(name="active")
    private int activeFlag;

    @JdbcColumn(name = "worker_extId")
    private String externalId;

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

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public CompanyDepartment getDepartment() {
        return department;
    }

    public void setDepartment(CompanyDepartment department) {
        this.department = department;
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

    public WorkerPosition getPosition() {
        return position;
    }

    public void setPosition(WorkerPosition position) {
        this.position = position;
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

    @Override
    public String getAuditType() {
        return "Worker";
    }

    public String getCompanyName() {
        return companyName;
    }

    public boolean isMain() {
        return activeFlag > 0;
    }
}
