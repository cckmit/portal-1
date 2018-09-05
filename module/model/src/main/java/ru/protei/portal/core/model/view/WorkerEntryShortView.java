package ru.protei.portal.core.model.view;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

/**
 * Сокращенное представление WorkerEntry
 */
@JdbcEntity(table = "worker_entry")
public class WorkerEntryShortView implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="personId")
    private Long personId;

    @JdbcJoinedColumn(localColumn = "companyId", table = "company", remoteColumn = "id", mappedColumn = "cname")
    private String companyName;

    @JdbcJoinedColumn(localColumn = "dep_id", table = "company_dep", remoteColumn = "id", mappedColumn = "dep_name")
    private String departmentName;

    @JdbcJoinedColumn(localColumn = "positionId", table = "worker_position", remoteColumn = "id", mappedColumn = "pos_name")
    private String positionName;

    @JdbcColumn(name="active")
    private int activeFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public int getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(int activeFlag) {
        this.activeFlag = activeFlag;
    }

    public boolean isMain() {
        return activeFlag > 0;
    }
}
