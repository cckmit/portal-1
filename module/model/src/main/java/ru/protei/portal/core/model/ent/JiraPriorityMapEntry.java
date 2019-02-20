package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;

@JdbcEntity(table = "jira_priority_map_entry")
public final class JiraPriorityMapEntry {

    @JdbcColumn(name = "id")
    private long id;

    @JdbcColumn(name = "MAP_ID")
    private long mapId;

    @JdbcColumn(name = "JIRA_priority_id")
    private int jiraPriorityId;

    @JdbcColumn(name = "LOCAL_priority_id")
    private int localPriorityId;

    @JdbcColumn(name = "LOCAL_priority_name")
    private String localPriorityName;

    @JdbcColumn(name = "JIRA_priority_name")
    private String jiraPriorityName;

    public String getJiraPriorityName() {
        return jiraPriorityName;
    }

    public void setJiraPriorityName(String jiraPriorityName) {
        this.jiraPriorityName = jiraPriorityName;
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

    public int getJiraPriorityId() {
        return jiraPriorityId;
    }

    public void setJiraPriorityId(int jiraPriorityId) {
        this.jiraPriorityId = jiraPriorityId;
    }

    public int getLocalPriorityId() {
        return localPriorityId;
    }

    public void setLocalPriorityId(int localPriorityId) {
        this.localPriorityId = localPriorityId;
    }

    public String getLocalPriorityName() {
        return localPriorityName;
    }

    public void setLocalPriorityName(String localPriorityName) {
        this.localPriorityName = localPriorityName;
    }
}
