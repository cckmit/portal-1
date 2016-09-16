package ru.protei.portal.webui.controller.ws.tools.migrate;

import protei.sql.Column;
import protei.sql.PrimaryKey;
import protei.sql.Table;
import ru.protei.portal.core.model.ent.CompanyDepartment;

/**
 * Created by turik on 08.09.16.
 */
@Table(name="OK.Tm_Department")
public class ExternalDepartment {

    private Long id;
    private String description;
    private Long bossId;
    private Long rootId;
    private Long typeId;
    private Long departmentId1C;

    public ExternalDepartment () {}

    public ExternalDepartment (CompanyDepartment department) {

        setDescription (department.getName ());
        setBossId (new Long(1));
        setRootId (new Long(0));
        setTypeId (new Long(1));
        setDepartmentId1C (department.getExternalId ());

    }

    @PrimaryKey
    @Column(name="nID")
    public Long getId() {
        return id;
    }

    @PrimaryKey
    @Column(name="nID")
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name="strDescription")
    public String getDescription() {
        return description;
    }

    @Column(name="strDescription")
    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="nBossID")
    public Long getBossId() {
        return bossId;
    }

    @Column(name="nBossID")
    public void setBossId(Long bossId) {
        this.bossId = bossId;
    }

    @Column(name="nRootID")
    public Long getRootId() {
        return rootId;
    }

    @Column(name="nRootID")
    public void setRootId(Long rootId) {
        this.rootId = rootId;
    }

    @Column(name="nTypeID")
    public Long getTypeId() {
        return typeId;
    }

    @Column(name="nTypeID")
    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    @Column(name="n1cDepartmentID")
    public Long getDepartmentId1C() {
        return departmentId1C;
    }

    @Column(name="n1cDepartmentID")
    public void setDepartmentId1C(Long departmentId1C) {
        this.departmentId1C = departmentId1C;
    }
}
