package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by butusov on 03.08.17.
 */
@JdbcEntity(table = "audit_object")
public class AuditObject implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "type")
    private int typeId;

    @JdbcColumn(name = "created")
    private Date created;

    @JdbcColumn(name = "creator")
    private Long creatorId;

    @JdbcColumn(name = "entry_info", converterType = ConverterType.JSON)
    private Serializable entryInfo;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId( int typeId ) {
        this.typeId = typeId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated( Date created ) {
        this.created = created;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId( Long creatorId ) {
        this.creatorId = creatorId;
    }

    public Serializable getEntryInfo() {
        return entryInfo;
    }

    public void setEntryInfo( Serializable entryInfo ) {
        this.entryInfo = entryInfo;
    }
}
