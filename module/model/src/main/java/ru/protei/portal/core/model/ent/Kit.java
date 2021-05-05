package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_DeliveryStatus;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.Objects;

@JdbcEntity(table = "kit")
public class Kit extends AuditableObject {
    public static final String AUDIT_TYPE = "Kit";

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "created")
    private Date created;

    @JdbcColumn(name = "modified")
    private Date modified;

    @JdbcColumn(name = "serial_number")
    private String serialNumber;

    @JdbcColumn(name = "name")
    private String name;

    @JdbcColumn(name = "state")
    @JdbcEnumerated(EnumType.ID)
    private En_DeliveryStatus state;

    public Kit() {}

    public Kit(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getAuditType() {
        return AUDIT_TYPE;
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

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public En_DeliveryStatus getState() {
        return state;
    }

    public void setState(En_DeliveryStatus state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Kit)) return false;
        Kit kit = (Kit) o;
        return id.equals(kit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Kit{" +
                "id=" + id +
                ", created=" + created +
                ", modified=" + modified +
                ", serialNumber='" + serialNumber + '\'' +
                ", name='" + name + '\'' +
                ", state=" + state +
                '}';
    }
}
