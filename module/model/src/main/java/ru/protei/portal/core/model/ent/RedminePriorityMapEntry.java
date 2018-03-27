package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;

@JdbcEntity(table = "redmine_priority_map_entry")
public final class RedminePriorityMapEntry {

    @JdbcColumn(name = "id")
    private long id;

    @JdbcColumn(name = "MAP_ID")
    private long mapId;

    @JdbcColumn(name = "RM_priority_id")
    private int redminePriorityId;

    @JdbcColumn(name = "LOCAL_priority_id")
    private int localPriorityId;

    @JdbcColumn(name = "LOCAL_priority_name")
    private String localPriorityName;

    @JdbcColumn(name = "RM_priority_name")
    private String redminePriorityName;

    public String getRedminePriorityName() {
        return redminePriorityName;
    }

    public void setRedminePriorityName(String redminePriorityName) {
        this.redminePriorityName = redminePriorityName;
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

    public int getRedminePriorityId() {
        return redminePriorityId;
    }

    public void setRedminePriorityId(int redminePriorityId) {
        this.redminePriorityId = redminePriorityId;
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
