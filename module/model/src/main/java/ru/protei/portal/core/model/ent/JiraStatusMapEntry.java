package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;

@JdbcEntity(table = "JIRA_status_map_entry")
public final class JiraStatusMapEntry {

    @JdbcColumn(name = "id")
    private long id;

    @JdbcColumn(name = "MAP_ID")
    private long mapId;

    @JdbcColumn(name = "jira_status_name")
    private String jiraStatusName;

    @JdbcColumn(name = "LOCAL_status_id")
    private int localStatusId;

    @JdbcColumn(name = "LOCAL_status_name")
    private String localStatusName;

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

    public int getLocalStatusId() {
        return localStatusId;
    }

    public void setLocalStatusId(int localStatusId) {
        this.localStatusId = localStatusId;
    }

    public String getLocalStatusName() {
        return localStatusName;
    }

    public void setLocalStatusName(String localStatusName) {
        this.localStatusName = localStatusName;
    }

    public En_CaseState getLocalStatus() {
        return En_CaseState.getById((long) localStatusId);
    }
}
