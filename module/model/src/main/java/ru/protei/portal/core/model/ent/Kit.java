package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.marker.HasLongId;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.Objects;

import static ru.protei.portal.core.model.ent.Kit.Columns.DELIVERY_ID;
import static ru.protei.portal.core.model.ent.Kit.Columns.ID;

@JdbcEntity(table = "kit")
public class Kit extends AuditableObject implements HasLongId {
    public static final String AUDIT_TYPE = "Kit";
    public static final String CASE_OBJECT_TABLE = "case_object";
    public static final String CASE_OBJECT_ALIAS = "CO";

    /**
     * Идентификатор
     */
    @JdbcId(name = ID, idInsertMode = IdInsertMode.EXPLICIT)
    private Long id;

    /**
     * Дата создания
     */
    @JdbcJoinedColumn(localColumn = ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.CREATED, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Date created;

    /**
     * Создатель
     */
    @JdbcJoinedColumn(localColumn = ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.CREATOR, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Long creatorId;

    /**
     * Дата изменения
     */
    @JdbcJoinedColumn(localColumn = ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.MODIFIED, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Date modified;

    /**
     * Название
     */
    @JdbcJoinedColumn(localColumn = ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.CASE_NAME, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private String name;

    /**
     * Идентификатор поставки
     */
    @JdbcColumn(name = DELIVERY_ID)
    private Long deliveryId;

    /**
     * Серийный номер
     */
    @JdbcColumn(name = "serial_number")
    private String serialNumber;

    /**
     * Статус комплекта
     */
    @JdbcJoinedColumn(localColumn = ID, remoteColumn = CaseObject.Columns.ID, mappedColumn = CaseObject.Columns.STATE,
            table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Long stateId;

    @JdbcJoinedObject(joinPath = {
            @JdbcJoinPath(localColumn = ID, remoteColumn = CaseObject.Columns.ID, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.STATE, remoteColumn = "id", table = "case_state", sqlTableAlias = CASE_OBJECT_ALIAS),
    })
    private CaseState state;

    @JdbcJoinedObject(joinPath = {
            @JdbcJoinPath(localColumn = ID, remoteColumn = CaseObject.Columns.ID, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.CREATOR, remoteColumn = "id", table = "person")})
    private Person creator;

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

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
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

    public Person getCreator() {
        return creator;
    }

    public void setState(CaseState state) {
        this.state = state;
        if (state != null) {
            this.stateId = state.getId();
        }
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
                ", creatorId=" + creatorId +
                ", modified=" + modified +
                ", name='" + name + '\'' +
                ", deliveryId=" + deliveryId +
                ", serialNumber='" + serialNumber + '\'' +
                ", stateId=" + stateId +
                ", state=" + state +
                ", creator=" + creator +
                '}';
    }

    public interface Columns {
        String ID = "id";
        String DELIVERY_ID = "delivery_id";
    }
}
