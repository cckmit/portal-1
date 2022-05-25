package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_LocationType;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

/**
 * Местоположение
 */
@JdbcEntity(table = "location")
public class Location implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="PARENT_ID")
    private int parentId;

    @JdbcColumn(name="TYPE_ID" )
    @JdbcEnumerated( EnumType.ID )
    private En_LocationType locationType;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getParentId() { return parentId; }

    public void setParentId(int parentId) { this.parentId = parentId; }

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
        return locationType;
    }

    public EntityOption toEntityOption() {
        return new EntityOption( name, id );
    }

    public String getCode() {
        return code;
    }

    public void setCode( String code ) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Location{" +
            "id=" + id +
            ", parentId=" + parentId +
            ", locationType=" + locationType +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", oldId=" + oldId +
            ", code='" + code + '\'' +
            ", path=" + path +
            '}';
    }
}
