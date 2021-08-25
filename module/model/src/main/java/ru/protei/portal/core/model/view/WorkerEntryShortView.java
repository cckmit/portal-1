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

    @JdbcJoinedColumn(mappedColumn = "displayShortName", localColumn = "personId", table = "person", remoteColumn = "id")
    private String personName;

    @JdbcColumn
    private Long companyId;

    @JdbcJoinedColumn(localColumn = "companyId", table = "company", remoteColumn = "id", mappedColumn = "cname")
    private String companyName;

    @JdbcJoinedColumn(localColumn = "companyId", table = "company", remoteColumn = "id", mappedColumn = "is_hidden")
    private Boolean companyIsHidden;

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

    @JdbcJoinedColumn(mappedColumn = "parent_dep", joinPath = {
            @JdbcJoinPath(localColumn = "dep_id", table = "company_dep", remoteColumn = "id"),
    })
    private Long parentDepId;

    @JdbcColumn(name = "dep_id")
    private Long depId;

    @JdbcColumn(name = "positionId")
    private Long positionId;

    @JdbcColumn(name = "worker_extId")
    private String workerExtId;

    @JdbcColumn(name = "is_contract_agreement")
    private boolean isContractAgreement;

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

    public String getPersonName() {
        return personName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getDepartmentParentName() {
        return departmentParentName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getPositionName() {
        return positionName;
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

    public Long getParentDepId() {
        return parentDepId;
    }

    public void setParentDepId(Long parentDepId) {
        this.parentDepId = parentDepId;
    }

    public Long getDepId() {
        return depId;
    }

    public void setDepId(Long depId) {
        this.depId = depId;
    }

    public Boolean getCompanyIsHidden() {
        return companyIsHidden;
    }

    public void setCompanyIsHidden(Boolean companyIsHidden) {
        this.companyIsHidden = companyIsHidden;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setDepartmentParentName(String departmentParentName) {
        this.departmentParentName = departmentParentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getWorkerExtId() {
        return workerExtId;
    }

    public void setWorkerExtId(String workerExtId) {
        this.workerExtId = workerExtId;
    }

    public boolean getContractAgreement() {
        return isContractAgreement;
    }

    public void setContractAgreement(boolean contractAgreement) {
        isContractAgreement = contractAgreement;
    }

    @Override
    public String toString() {
        return "WorkerEntryShortView{" +
                "id=" + id +
                ", personId=" + personId +
                ", personName='" + personName + '\'' +
                ", companyId=" + companyId +
                ", companyName='" + companyName + '\'' +
                ", companyIsHidden=" + companyIsHidden +
                ", departmentParentName='" + departmentParentName + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", positionName='" + positionName + '\'' +
                ", activeFlag=" + activeFlag +
                ", parentDepId=" + parentDepId +
                ", depId=" + depId +
                ", positionId=" + positionId +
                ", workerExtId='" + workerExtId + '\'' +
                ", isContractAgreement=" + isContractAgreement +
                '}';
    }
}
