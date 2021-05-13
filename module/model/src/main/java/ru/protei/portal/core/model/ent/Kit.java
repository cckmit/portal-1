package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.Objects;

import static ru.protei.portal.core.model.ent.Delivery.*;

@JdbcEntity(table = "kit")
public class Kit extends AuditableObject {
    public static final String AUDIT_TYPE = "Kit";

    /**
     * Идентификатор
     */
    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    /**
     * Идентификатор поставки
     */
    @JdbcColumn(name = "delivery_id")
    private Long deliveryId;

    /**
     * Дата создания
     */
    @JdbcColumn(name = "created")
    private Date created;

    /**
     * Дата изменения
     */
    @JdbcColumn(name = "modified")
    private Date modified;

    /**
     * Серийный номер
     */
    @JdbcColumn(name = "serial_number")
    private String serialNumber;

    /**
     * Название
     */
    @JdbcColumn(name = "name")
    private String name;

    /**
     * Идентификатор статуса
     */
    @JdbcColumn(name = "state")
    @JdbcEnumerated(EnumType.ID)
    private Long stateId;

    /**
     * Статус
     */
    @JdbcJoinedObject(joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = Columns.STATE, remoteColumn = "id", table = "case_state", sqlTableAlias = CASE_OBJECT_ALIAS),
    })
    private CaseState state;

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

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
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

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public CaseState getState() {
        return state;
    }

    public void setState(CaseState state) {
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
                ", deliveryId=" + deliveryId +
                ", created=" + created +
                ", modified=" + modified +
                ", serialNumber='" + serialNumber + '\'' +
                ", name='" + name + '\'' +
                ", stateId=" + stateId +
                ", state=" + state +
                '}';
    }
}
