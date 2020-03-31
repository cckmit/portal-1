package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.util.Date;

@JdbcEntity(table = "redmine_endpoint")
public final class RedmineEndpoint {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "server_addr")
    private String serverAddress;

    @JdbcColumn(name = "project_id")
    private String projectId;

    @JdbcColumn(name = "api_key")
    private String apiKey;

    @JdbcColumn(name = "COMPANY_ID")
    private Long companyId;

    @JdbcColumn(name = "last_created")
    private Date lastCreatedOnDate;

    @JdbcColumn(name = "last_updated")
    private Date lastUpdatedOnDate;

    @JdbcColumn(name = "STATUS_MAP_ID")
    private long statusMapId;

    @JdbcColumn(name = "PRIORITY_MAP_ID")
    private long priorityMapId;

    @JdbcColumn(name = "DEFAULT_USER_ID")
    private Integer defaultUserId;

    @JdbcColumn(name = "DEFAULT_USER_LOCAL_ID")
    private Long defaultUserLocalId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Date getLastCreatedOnDate() {
        return lastCreatedOnDate;
    }

    public void setLastCreatedOnDate(Date lastCreatedOnDate) {
        this.lastCreatedOnDate = lastCreatedOnDate;
    }

    public Date getLastUpdatedOnDate() {
        return lastUpdatedOnDate;
    }

    public void setLastUpdatedOnDate(Date lastUpdatedOnDate) {
        this.lastUpdatedOnDate = lastUpdatedOnDate;
    }

    public long getStatusMapId() {
        return statusMapId;
    }

    public void setStatusMapId(long statusMapId) {
        this.statusMapId = statusMapId;
    }

    public long getPriorityMapId() {
        return priorityMapId;
    }

    public void setPriorityMapId(long priorityMapId) {
        this.priorityMapId = priorityMapId;
    }

    public Integer getDefaultUserId() {
        return defaultUserId;
    }

    public void setDefaultUserId(Integer defaultUserId) {
        this.defaultUserId = defaultUserId;
    }

    public Long getDefaultUserLocalId() {
        return defaultUserLocalId;
    }

    public void setDefaultUserLocalId(Long defaultUserLocalId) {
        this.defaultUserLocalId = defaultUserLocalId;
    }

    @Override
    public String toString() {
        return "RedmineEndpoint{" +
                "id=" + id +
                ", serverAddress='" + serverAddress + '\'' +
                ", projectId='" + projectId + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", companyId=" + companyId +
                ", lastCreatedOnDate=" + lastCreatedOnDate +
                ", lastUpdatedOnDate=" + lastUpdatedOnDate +
                ", statusMapId=" + statusMapId +
                ", priorityMapId=" + priorityMapId +
                ", defaultUserId=" + defaultUserId +
                ", defaultUserLocalId=" + defaultUserLocalId +
                '}';
    }
}
