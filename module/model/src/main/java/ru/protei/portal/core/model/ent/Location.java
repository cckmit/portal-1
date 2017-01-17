package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_LocationType;
import ru.protei.portal.core.model.struct.DistrictInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.core.model.view.ProductShortViewSupport;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Местоположение
 */
@JdbcEntity(table = "location")
public class Location implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="PARENT_ID")
    private int parentId;

    @JdbcColumn(name="TYPE_ID")
    private int typeId;

    @JdbcColumn(name="NAME")
    private String name;

    @JdbcColumn(name="DESCRIPTION")
    private String description;

    @JdbcColumn(name="OLD_ID")
    private Long oldId;

    @JdbcColumn(name="CODE")
    private String code;

    @JdbcColumn(name="PATH")
    private String path;

    public Location() {}

    public Location( Long id ) {
        this.id = id;
    }

    public Location( int typeId, String name, String description ) {
        this.typeId = typeId;
        this.name = name;
        this.description = description;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOldId() {
        return oldId;
    }

    public void setOldId(Long oldId) {
        this.oldId = oldId;
    }

    public En_LocationType getType () {
        return En_LocationType.forId(this.typeId);
    }

    public DistrictInfo toDistrictInfo() {
        return new DistrictInfo(this.id, this.name, this.code);
    }

    @Override
    public String toString() {
        return "Location{" +
            "id=" + id +
            ", parentId=" + parentId +
            ", typeId=" + typeId +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", oldId=" + oldId +
            ", code='" + code + '\'' +
            ", path=" + path +
            '}';
    }
}
