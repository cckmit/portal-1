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

    @JdbcColumn(name = "JIRA_status_id")
    private int jiraStatusId;

    @JdbcColumn(name = "LOCAL_status_id")
    private int localStatusId;

    @JdbcColumn(name = "LOCAL_status_name")
    private String localStatusName;

    @JdbcColumn(name = "LOCAL_previous_status_id")
    private int localOldStatusId;

    public int getLocalOldStatusId() {
        return localOldStatusId;
    }

    public void setLocalOldStatusId(int localOldStatusId) {
        this.localOldStatusId = localOldStatusId;
    }

    public long getId() {
        return id;
    }

    public long getMapId() {
        return mapId;
    }

    public void setMapId(long mapId) {
        this.mapId = mapId;
    }

    public int getJiraStatusId() {
        return jiraStatusId;
    }

    public void setJiraStatusId(int jiraStatusId) {
        this.jiraStatusId = jiraStatusId;
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

    public En_CaseState getOldLocalStatus() {
        return En_CaseState.getById((long) localOldStatusId);
    }
}
