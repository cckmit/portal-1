package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;

@JdbcEntity(table = "export_syb_entry")
public class ExportSybEntry {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "created")
    private Date created;

    @JdbcColumn(name = "instance_id")
    private String instanceId;

    @JdbcColumn(name = "local_id")
    private Long localId;

    @JdbcColumn(name = "obj_type")
    private String entityType;

    @JdbcColumn( name = "obj_dump", converterType = ConverterType.JSON )
    private AuditableObject entry;

    public ExportSybEntry() {
    }

    public ExportSybEntry(AuditableObject entry, String instanceId) {
        this.created = new Date();
        this.instanceId = instanceId;
        setEntry(entry);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Long getLocalId() {
        return localId;
    }

    public String getEntityType() {
        return entityType;
    }

    public AuditableObject getEntry() {
        return entry;
    }

    public void setEntry(AuditableObject entry) {
        this.entry = entry;
        this.entityType = entry.getAuditType();
        this.localId = entry.getId();
    }


    @Override
    public String toString() {
        return new StringBuilder("exportEntry{id=").append(getId())
                .append(", type=").append(getEntityType())
                .append(", local-id=").append(getLocalId())
                .append("}")
                .toString();
    }
}
