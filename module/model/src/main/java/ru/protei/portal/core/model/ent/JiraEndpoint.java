package ru.protei.portal.core.model.ent;

import protei.sql.Table;
import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.util.Date;

@Table(name = "nexign_endpoint")
public class JiraEndpoint {
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

    @JdbcColumn(name = "person_id")
    private Long personId;

    @JdbcColumn(name = "STATUS_MAP_ID")
    private long statusMapId;

    @JdbcColumn(name = "PRIORITY_MAP_ID")
    private long priorityMapId;

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

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }
}
