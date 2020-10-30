package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.List;

@JdbcEntity(table = "platform")
public class Platform extends AuditableObject {
    public static final String AUDIT_TYPE = "Platform";

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
    private PersonShortView manager;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "project_id", remoteColumn = "id", table = "case_object"),
            @JdbcJoinPath(localColumn = "MANAGER", remoteColumn = "id", table = "person")
    }, mappedColumn = "displayShortName")
    private String caseManagerShortName;

    @JdbcJoinedObject(localColumn = "company_id", remoteColumn = "id")
    private Company company;

    @JdbcColumn(name = "project_id")
    private Long projectId;

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

    public PersonShortView getManager() {
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

    public void setManager(PersonShortView manager) {
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

    public static Platform fromPlatformOption(PlatformOption platformOption) {
        if (platformOption == null) {
            return null;
        }

        Platform platform = new Platform();
        platform.setId(platformOption.getId());
        platform.setName(platformOption.getDisplayText());
        return platform;
    }

    public PlatformOption toPlatformOption() {
        PlatformOption platformOption = new PlatformOption();
        platformOption.setId(getId());
        platformOption.setDisplayText(getName());
        platformOption.setCompanyId(getCompanyId());
        return platformOption;
    }

    @Override
    public String getAuditType() {
        return AUDIT_TYPE;
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
                ", caseManagerShortName='" + caseManagerShortName + '\'' +
                ", company=" + company +
                ", projectId=" + projectId +
                ", caseId=" + caseId +
                ", attachments=" + attachments +
                ", serversCount=" + serversCount +
                '}';
    }
}
