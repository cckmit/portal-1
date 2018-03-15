package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;

@JdbcEntity(table = "redmine_status_map_entry")
public final class RedmineStatusMapEntry {

    @JdbcColumn(name = "id")
    private long id;

    @JdbcColumn(name = "MAP_ID")
    private long mapId;

    @JdbcColumn(name = "RM_status_id")
    private int redmineStatusId;

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

    public int getRedmineStatusId() {
        return redmineStatusId;
    }

    public void setRedmineStatusId(int redmineStatusId) {
        this.redmineStatusId = redmineStatusId;
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
}
