package ru.protei.portal.core.model.view;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

/**
 * Сокращенное представление WorkerEntry
 */
@JdbcEntity(table = "worker_entry")
public class WorkerEntryShortView implements Serializable {

    @JdbcId(idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn
    private Long personId;

    @JdbcColumn
    private Long companyId;

    @JdbcJoinedColumn(localColumn = "companyId", table = "company", remoteColumn = "id", mappedColumn = "cname")
    private String companyName;

    @JdbcJoinedColumn(mappedColumn = "dep_name", joinPath = {
            @JdbcJoinPath(localColumn = "dep_id", table = "company_dep", remoteColumn = "id"),
            @JdbcJoinPath(localColumn = "parent_dep", table = "company_dep", remoteColumn = "id")
    })
    private String departmentParentName;

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

    public String getDepartmentParentName() {
        return departmentParentName;
    }

    public void setDepartmentParentName(String departmentParentName) {
        this.departmentParentName = departmentParentName;
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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public boolean isMain() {
        return activeFlag > 0;
    }
}
