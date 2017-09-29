package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;

/**
 * Created by michael on 24.05.16.
 */
@JdbcEntity(table = "company_dep")
public class CompanyDepartment {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "company_id")
    private Long companyId;

    @JdbcJoinedColumn(localColumn = "company_id", table = "company_group_home", remoteColumn = "companyId", mappedColumn = "external_code")
    private String externalCode;

    @JdbcColumn(name = "created")
    private Date created;

    @JdbcColumn(name = "depTypeId")
    private int typeId;

    @JdbcColumn(name = "dep_name")
    private String name;

    @JdbcColumn(name = "dep_info")
    private String info;

    @JdbcColumn(name = "parent_dep")
    private Long parentId;

    @JdbcJoinedColumn(localColumn = "parent_dep", table = "company_dep", remoteColumn = "id", mappedColumn = "dep_extId")
    private Long parentExternalId;

    @JdbcColumn(name = "head_id")
    private Long headId;

    @JdbcColumn(name = "dep_extId")
    private Long externalId;

    public CompanyDepartment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getHeadId() {
        return headId;
    }

    public void setHeadId(Long headId) {
        this.headId = headId;
    }

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    public String getExternalCode() {
        return externalCode;
    }

    public void setExternalCode(String externalCode) {
        this.externalCode = externalCode;
    }

    public Long getParentExternalId() {
        return parentExternalId;
    }

    public void setParentExternalId(Long parentExternalId) {
        this.parentExternalId = parentExternalId;
    }
}
