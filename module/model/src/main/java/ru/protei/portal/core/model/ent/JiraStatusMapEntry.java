package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcJoinedColumn;

import java.io.Serializable;

@JdbcEntity(table = "jira_status_map_entry")
public final class JiraStatusMapEntry implements Serializable {

    @JdbcColumn(name = "id")
    private long id;

    @JdbcColumn(name = "MAP_ID")
    private long mapId;

    @JdbcColumn(name = "jira_status_name")
    private String jiraStatusName;

    @JdbcColumn(name = "LOCAL_status_id")
    private Long localStatusId;

    @JdbcColumn(name = "LOCAL_status_name")
    private String localStatusName;

    @JdbcJoinedColumn(localColumn = "LOCAL_status_id", remoteColumn = "id", table = "case_state", mappedColumn = "info")
    private String info;

    public long getId() {
        return id;
    }

    public long getMapId() {
        return mapId;
    }

    public void setMapId(long mapId) {
        this.mapId = mapId;
    }

    public String getJiraStatusName() {
        return jiraStatusName;
    }

    public void setJiraStatusName(String jiraStatusName) {
        this.jiraStatusName = jiraStatusName;
    }

    public Long getLocalStatusId() {
        return localStatusId;
    }

    public void setLocalStatusId(Long localStatusId) {
        this.localStatusId = localStatusId;
    }

    public String getLocalStatusName() {
        return localStatusName;
    }

    public void setLocalStatusName(String localStatusName) {
        this.localStatusName = localStatusName;
    }
    
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "JiraStatusMapEntry{" +
                "id=" + id +
                ", mapId=" + mapId +
                ", jiraStatusName='" + jiraStatusName + '\'' +
                ", localStatusId=" + localStatusId +
                ", localStatusName='" + localStatusName + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}
