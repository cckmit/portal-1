package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

@JdbcEntity(table = "redmine_to_crm_status_map_entry")
public final class RedmineToCrmEntry {
    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTOINCREMENT)
    private Long id;

    @JdbcColumn(name = "MAP_ID")
    private Long mapId;

    @JdbcColumn(name = "RM_status_id")
    private Integer redmineStatusId;

    @JdbcColumn(name = "LOCAL_status_id")
    private Integer localStatusId;

    @JdbcJoinedColumn(localColumn = "LOCAL_status_id", table = "case_state", remoteColumn = "id", mappedColumn = "STATE")
    private String localStatusName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMapId() {
        return mapId;
    }

    public void setMapId(Long mapId) {
        this.mapId = mapId;
    }

    public Integer getRedmineStatusId() {
        return redmineStatusId;
    }

    public void setRedmineStatusId(Integer redmineStatusId) {
        this.redmineStatusId = redmineStatusId;
    }

    public Integer getLocalStatusId() {
        return localStatusId;
    }

    public void setLocalStatusId(Integer localStatusId) {
        this.localStatusId = localStatusId;
    }

    public String getLocalStatusName() {
        return localStatusName;
    }

    public void setLocalStatusName(String localStatusName) {
        this.localStatusName = localStatusName;
    }
}
