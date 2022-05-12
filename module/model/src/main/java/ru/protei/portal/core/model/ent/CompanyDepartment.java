package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.Objects;

/**
 * Created by michael on 24.05.16.
 */
@JdbcEntity(table = "company_dep")
public class CompanyDepartment extends AuditableObject {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "company_id")
    private Long companyId;

    @JdbcJoinedColumn(localColumn = "company_id", table = "company_group_home", remoteColumn = "companyId", mappedColumn = "external_code")
    private String companyExternalCode;

    @JdbcColumn(name = "created")
    private Date created;

    @JdbcColumn(name = "dep_name")
    private String name;

    @JdbcColumn(name = "dep_info")
    private String info;

    @JdbcColumn(name = "parent_dep")
    private Long parentId;

    @JdbcJoinedColumn(localColumn = "parent_dep", table = "company_dep", remoteColumn = "id", mappedColumn = "dep_extId")
    private String parentExternalId;

    @JdbcJoinedObject(joinPath = {
            @JdbcJoinPath(localColumn = "parent_dep", table = "company_dep", remoteColumn = "id"),
            @JdbcJoinPath(localColumn = "head_id", table = "worker_entry", remoteColumn = "id"),
            @JdbcJoinPath(localColumn = "personId", table = "person", remoteColumn = "id")
    })
    private PersonShortView parentHead;

    @JdbcColumn(name = "head_id")
    private Long headId;

    @JdbcJoinedColumn(localColumn = "head_id", table = "worker_entry", remoteColumn = "id", mappedColumn = "worker_extId")
    private String headExternalId;

    @JdbcJoinedObject(joinPath = {
            @JdbcJoinPath(localColumn = "head_id", table = "worker_entry", remoteColumn = "id"),
            @JdbcJoinPath(localColumn = "personId", table = "person", remoteColumn = "id")
    })
    private PersonShortView head;

    @JdbcColumn(name = "dep_extId")
    private String externalId;

    public CompanyDepartment() {
    }

    public CompanyDepartment(Long id, String name) {
        this.id = id;
        this.name = name;
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

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getCompanyExternalCode() {
        return companyExternalCode;
    }

    public void setCompanyExternalCode(String companyExternalCode) {
        this.companyExternalCode = companyExternalCode;
    }

    public String getParentExternalId() {
        return parentExternalId;
    }

    public void setParentExternalId(String parentExternalId) {
        this.parentExternalId = parentExternalId;
    }

    public String getHeadExternalId() {
        return headExternalId;
    }

    public void setHeadExternalId(String headExternalId) {
        this.headExternalId = headExternalId;
    }

    public PersonShortView getParentHead() {
        return parentHead;
    }

    public void setParentHead( PersonShortView parentHead ) {
        this.parentHead = parentHead;
    }

    public PersonShortView getHead() {
        return head;
    }

    public void setHead( PersonShortView head ) {
        this.head = head;
    }

    @Override
    public String getAuditType() {
        return "Department";
    }

    public static EntityOption toOption(CompanyDepartment department) {
        return new EntityOption(department.getName(), department.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyDepartment that = (CompanyDepartment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
