package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by michael on 23.05.16.
 */
@JdbcEntity(table = "dev_unit")
public class DevUnit implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="UTYPE_ID")
    private int typeId;

    @JdbcColumn(name="CREATED")
    private Date created;

    @JdbcColumn(name="UNIT_NAME")
    private String name;

    @JdbcColumn(name="UNIT_INFO")
    private String info;

    @JdbcColumn(name="LAST_UPDATE")
    private Date lastUpdate;

    @JdbcColumn(name="CREATOR_ID")
    private Long creatorId;

    @JdbcColumn(name="UNIT_STATE")
    private int stateId;

    @JdbcColumn(name = "old_id")
    private Long oldId;

    public DevUnit () {

    }

    public DevUnit(int typeId, String name, String info) {
        this.typeId = typeId;
        this.name = name;
        this.info = info;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public Long getOldId() {
        return oldId;
    }

    public void setOldId(Long oldId) {
        this.oldId = oldId;
    }

    public En_DevUnitState getState () {
        return En_DevUnitState.forId(this.stateId);
    }

    public boolean isActiveUnit () {
        return getState() == En_DevUnitState.ACTIVE;
    }

    public En_DevUnitType getType () {
        return En_DevUnitType.forId(this.typeId);
    }

    @Override
    public String toString() {
        return "DevUnit{" +
                "id=" + id +
                ", typeId=" + typeId +
                ", created=" + created +
                ", name='" + name + '\"' +
                ", info='" + info + '\"' +
                ", lastUpdate=" + lastUpdate +
                ", creatorId=" + creatorId +
                ", stateId=" + stateId +
                ", oldId=" + oldId +
                '}';
    }
}
