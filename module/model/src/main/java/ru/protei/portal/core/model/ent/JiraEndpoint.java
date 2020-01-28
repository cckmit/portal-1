package ru.protei.portal.core.model.ent;

import protei.sql.Table;
import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.util.Date;

@Table(name = "jira_endpoint")
public class JiraEndpoint {
    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "server_addr")
    private String serverAddress;

    @JdbcColumn(name = "project_id")
    private String projectId;

    @JdbcColumn(name = "COMPANY_ID")
    private Long companyId;

    @JdbcColumn(name = "person_id")
    private Long personId;

    @JdbcColumn(name = "STATUS_MAP_ID")
    private long statusMapId;

    @JdbcColumn(name = "PRIORITY_MAP_ID")
    private long priorityMapId;

    @JdbcColumn(name = "SLA_MAP_ID")
    private long slaMapId;

    @JdbcColumn(name = "server_login")
    private String serverLogin;

    @JdbcColumn(name = "server_pwd")
    private String serverPassword;

    public JiraEndpoint() {
    }

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

    public long getSlaMapId() {
        return slaMapId;
    }

    public void setSlaMapId(long slaMapId) {
        this.slaMapId = slaMapId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }


    public String getServerLogin() {
        return serverLogin;
    }

    public void setServerLogin(String serverLogin) {
        this.serverLogin = serverLogin;
    }

    public String getServerPassword() {
        return serverPassword;
    }

    public void setServerPassword(String serverPassword) {
        this.serverPassword = serverPassword;
    }

    @Override
    public String toString() {
        return "JiraEndpoint{" +
                "id=" + id +
                ", companyId=" + companyId +
                '}';
    }
}
