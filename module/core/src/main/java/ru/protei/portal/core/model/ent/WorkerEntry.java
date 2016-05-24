package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.util.Date;

/**
 * Created by michael on 17.05.16.
 */
@JdbcEntity(table = "worker_entry")
public class WorkerEntry {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="created")
    private Date created;

    @JdbcColumn(name="personId")
    private Long personId;

    @JdbcColumn(name="dep_id")
    private Long departmentId;

    @JdbcColumn(name="companyId")
    private Long companyId;

    @JdbcColumn(name="positionId")
    private Long positionId;

    @JdbcColumn(name="hireDate")
    private Date hireDate;

    @JdbcColumn(name="fireDate")
    private Date fireDate;

    @JdbcColumn(name="hireOrderNo")
    private String hireOrderNo;

    @JdbcColumn(name="fireOrderNo")
    private String fireOrderNo;

    @JdbcColumn(name="active")
    private int activeFlag;

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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public Date getFireDate() {
        return fireDate;
    }

    public void setFireDate(Date fireDate) {
        this.fireDate = fireDate;
    }

    public String getHireOrderNo() {
        return hireOrderNo;
    }

    public void setHireOrderNo(String hireOrderNo) {
        this.hireOrderNo = hireOrderNo;
    }

    public String getFireOrderNo() {
        return fireOrderNo;
    }

    public void setFireOrderNo(String fireOrderNo) {
        this.fireOrderNo = fireOrderNo;
    }

    public int getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(int activeFlag) {
        this.activeFlag = activeFlag;
    }

}
