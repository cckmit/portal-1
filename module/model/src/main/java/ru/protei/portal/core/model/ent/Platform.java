package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

@JdbcEntity(table = "platform")
public class Platform implements Serializable, Removable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="company_id")
    private Long companyId;

    @JdbcColumn(name="name")
    private String name;

    @JdbcColumn(name="parameters")
    private String params;

    @JdbcColumn(name="comment")
    private String comment;

    @JdbcJoinedObject(localColumn = "manager_id", remoteColumn = "id", updateLocalColumn = true)
    private Person manager;

    @JdbcJoinedObject(localColumn = "company_id", remoteColumn = "id")
    private Company company;

    @JdbcJoinedColumn(table = "case_object", mappedColumn = "id", joinData = {
            @JdbcJoinData(remoteColumn = "case_type", value = "13"), //En_CaseType.SF_PLATFORM
            @JdbcJoinData(localColumn = "id", remoteColumn = "CASENO")
    })
    private Long caseId;

    private Long serversCount;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Long getServersCount() {
        return serversCount;
    }

    public void setServersCount(Long serversCount) {
        this.serversCount = serversCount;
    }

    public Person getManager() {
        return manager;
    }

    public void setManager(Person manager) {
        this.manager = manager;
    }

    public Long getCaseId() {
        return caseId;
    }

    public static Platform fromEntityOption(EntityOption entityOption) {
        if (entityOption == null) {
            return null;
        }

        Platform platform = new Platform();
        platform.setId(entityOption.getId());
        platform.setName(entityOption.getDisplayText());
        return platform;
    }

    public EntityOption toEntityOption() {
        EntityOption entityOption = new EntityOption();
        entityOption.setId(getId());
        entityOption.setDisplayText(getName());
        return entityOption;
    }


    @Override
    public boolean isAllowedRemove() {
        return id != null;
    }

    @Override
    public String toString() {
        return "Platform{" +
                "id=" + id +
                ", companyId=" + companyId +
                ", name='" + name + '\'' +
                ", params='" + params + '\'' +
                ", comment='" + comment + '\'' +
                ", manager=" + manager +
                ", company=" + company +
                ", caseId=" + caseId +
                ", serversCount=" + serversCount +
                '}';
    }
}
