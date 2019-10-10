package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.List;

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

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "project_id", remoteColumn = "id", table = "case_object"),
            @JdbcJoinPath(localColumn = "MANAGER", remoteColumn = "id", table = "Person")
    }, mappedColumn = "displayShortName")
    private String caseManagerShortName;

    @JdbcJoinedObject(localColumn = "company_id", remoteColumn = "id")
    private Company company;

    @JdbcColumn(name = "project_id")
    private Long projectId;

    @JdbcJoinedColumn(localColumn = "project_id", table = "case_object", remoteColumn = "id", mappedColumn = "CASE_NAME", sqlTableAlias = "case_object")
    private String projectName;

    @JdbcColumn(name="case_id")
    private Long caseId;

    @JdbcManyToMany(localColumn = "case_id", linkTable = "case_attachment", localLinkColumn = "case_id", remoteLinkColumn = "att_id")
    private List<Attachment> attachments;

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

    public String getCaseManagerShortName() {
        return caseManagerShortName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setManager(Person manager) {
        this.manager = manager;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public Long getCaseId() {
        return caseId;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
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
    public boolean equals(Object obj) {
        if (id != null) {
            return obj instanceof Platform && id.equals(((Platform) obj).getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
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
                ", projectId=" + projectId +
                ", projectName=" + projectName +
                '}';
    }
}
