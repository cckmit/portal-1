package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.winter.jdbc.annotations.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by butusov on 03.08.17.
 */
@JdbcEntity( table = "audit" )
public class AuditObject implements Serializable {

    @JdbcId( name = "id", idInsertMode = IdInsertMode.AUTO )
    private Long id;

    @JdbcColumn( name = "type" )
    @JdbcEnumerated(EnumType.ID)
    private En_AuditType type;

    @JdbcColumn( name = "created" )
    private Date created;

    @JdbcColumn( name = "creator" )
    private Long creatorId;

    @JdbcJoinedObject( localColumn = "creator", remoteColumn = "id", updateLocalColumn = false )
    private Person creator;

    @JdbcColumn(name = "creator_ip")
    private String creatorIp;

    @JdbcColumn(name = "creator_shortname")
    private String creatorShortName;

    @JdbcColumn( name = "entry_info", converterType = ConverterType.JSON )
    private AuditableObject entryInfo;

    public AuditObject() {}

    public AuditObject(En_AuditType auditType, AuditableObject auditableObject, Long creatorId, String creatorIp, String creatorShortName) {
        setCreated(new Date());
        setType(auditType);
        setCreatorId(creatorId);
        setCreatorIp(creatorIp);
        setCreatorShortName(creatorShortName);
        setEntryInfo(auditableObject);
    }

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public En_AuditType getType() {
        return type;
    }

    public void setType( En_AuditType type ) {
        this.type = type;
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

    public Person getCreator() {
        return creator;
    }

    public String getCreatorIp() {
        return creatorIp;
    }

    public void setCreatorIp( String creatorIp ) {
        this.creatorIp = creatorIp;
    }

    public String getCreatorShortName() {
        return creatorShortName;
    }

    public void setCreatorShortName( String creatorShortName ) {
        this.creatorShortName = creatorShortName;
    }

    public AuditableObject getEntryInfo() {
        return entryInfo;
    }

    public void setEntryInfo( AuditableObject entryInfo ) {
        this.entryInfo = entryInfo;
    }
}
