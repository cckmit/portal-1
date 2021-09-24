package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.Objects;

import static ru.protei.portal.core.model.ent.Module.Columns.*;


@JdbcEntity(table = "module")
public class Module extends AuditableObject {
    public static final String AUDIT_TYPE = "Module";
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

    @JdbcJoinedObject(joinPath = {
            @JdbcJoinPath(localColumn = ID, remoteColumn = CaseObject.Columns.ID, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.CREATOR, remoteColumn = "id", table = "person")})
    private Person creator;

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
     * Описание
     */
    @JdbcJoinedColumn(localColumn = ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.INFO, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private String description;

    /**
     * Статус модуля
     */
    @JdbcJoinedColumn(localColumn = ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.STATE, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private long stateId;

    @JdbcJoinedObject(joinPath = {
            @JdbcJoinPath(localColumn = ID, remoteColumn = CaseObject.Columns.ID, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.STATE, remoteColumn = "id", table = "case_state", sqlTableAlias = CASE_OBJECT_ALIAS),
    })
    private CaseState state;

    /**
     * Серийный номер
     */
    @JdbcColumn(name = SERIAL_NUMBER)
    private String serialNumber;

    @JdbcColumn(name = KIT_ID)
    private Long kitId;

    @JdbcJoinedColumn(localColumn = KIT_ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.STATE, table = CASE_OBJECT_TABLE, sqlTableAlias = "KCO")
    private Long kitStateId;

    @JdbcColumn(name = "parent_module_id")
    private Long parentModuleId;

    /**
     * Заказчик
     */
    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "kit_id", remoteColumn = "id", table = "kit"),
            @JdbcJoinPath(localColumn = "delivery_id", remoteColumn = "id", table = "delivery"),
            @JdbcJoinPath(localColumn = "project_id", remoteColumn = "id", table = "project"),
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = "initiator_company", remoteColumn = "id", table = "company")}, mappedColumn = "cname")
    private String customerName;

    /**
     * Менеджер
     */
    @JdbcJoinedObject(joinPath = {
            @JdbcJoinPath(localColumn = "kit_id", remoteColumn = "id", table = "kit"),
            @JdbcJoinPath(localColumn = "delivery_id", remoteColumn = "id", table = "delivery"),
            @JdbcJoinPath(localColumn = "project_id", remoteColumn = "id", table = "project"),
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = Project.Columns.MANAGER, remoteColumn = "id", table = "person")})
    private Person manager;

    @JdbcJoinedColumn(localColumn = Delivery.Columns.ID, remoteColumn = Delivery.Columns.ID, mappedColumn = CaseObject.Columns.DELETED, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private boolean deleted;

    /**
     * Ответственный АО
     */
    @JdbcColumn(name = HW_MANAGER)
    private Long hwManagerId;

    @JdbcJoinedObject(localColumn = HW_MANAGER, remoteColumn = "id")
    private PersonShortView hwManager;

    /**
     * Ответственный КК
     */
    @JdbcColumn(name = QC_MANAGER)
    private Long qcManagerId;

    @JdbcJoinedObject(localColumn = QC_MANAGER, remoteColumn = "id")
    private PersonShortView qcManager;

    /**
     * Дата отправки
     */
    @JdbcColumn(name = "departure_date")
    private Date departureDate;

    /**
     * Дата сборки
     */
    @JdbcColumn(name = "build_date")
    private Date buildDate;

    @Override
    public String getAuditType() {
        return AUDIT_TYPE;
    }

    @Override
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

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Person getCreator() {
        return creator;
    }

    public void setCreator(Person creator) {
        this.creator = creator;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
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

    public long getStateId() {
        return stateId;
    }

    public void setStateId(long stateId) {
        this.stateId = stateId;
    }

    public CaseState getState() {
        return state;
    }

    public void setState(CaseState state) {
        this.state = state;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Long getKitId() {
        return kitId;
    }

    public void setKitId(Long kitId) {
        this.kitId = kitId;
    }

    public Long getKitStateId() {
        return kitStateId;
    }

    public void setKitStateId(Long kitStateId) {
        this.kitStateId = kitStateId;
    }

    public Long getParentModuleId() {
        return parentModuleId;
    }

    public void setParentModuleId(Long parentModuleId) {
        this.parentModuleId = parentModuleId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Person getManager() {
        return manager;
    }

    public void setManager(Person manager) {
        this.manager = manager;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Long getHwManagerId() {
        return hwManagerId;
    }

    public void setHwManagerId(Long hwManagerId) {
        this.hwManagerId = hwManagerId;
    }

    public PersonShortView getHwManager() {
        return hwManager;
    }

    public void setHwManager(PersonShortView hwManager) {
        this.hwManager = hwManager;
    }

    public Long getQcManagerId() {
        return qcManagerId;
    }

    public void setQcManagerId(Long qcManagerId) {
        this.qcManagerId = qcManagerId;
    }

    public PersonShortView getQcManager() {
        return qcManager;
    }

    public void setQcManager(PersonShortView qcManager) {
        this.qcManager = qcManager;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public Date getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(Date buildDate) {
        this.buildDate = buildDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Module module = (Module) o;
        return Objects.equals(id, module.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Module{" +
                "id=" + id +
                ", created=" + created +
                ", creatorId=" + creatorId +
                ", modified=" + modified +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", stateId=" + stateId +
                ", state=" + state +
                ", serialNumber='" + serialNumber + '\'' +
                ", kitId=" + kitId +
                ", parentModuleId=" + parentModuleId +
                ", customerName='" + customerName + '\'' +
                ", deleted=" + deleted +
                ", managerName='" + manager + '\'' +
                ", hwManagerId=" + hwManagerId +
                ", qcManagerId=" + qcManagerId +
                ", departureDate=" + departureDate +
                ", buildDate=" + buildDate +
                '}';
    }

    public interface Columns {
        String ID = "id";
        String SERIAL_NUMBER = "serial_number";
        String KIT_ID = "kit_id";
        String HW_MANAGER = "hw_manager_id";
        String QC_MANAGER = "qc_manager_id";
    }
}
