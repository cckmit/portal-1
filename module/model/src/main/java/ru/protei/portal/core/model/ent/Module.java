package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.Objects;

import static ru.protei.portal.core.model.ent.Delivery.Columns.HW_MANAGER;
import static ru.protei.portal.core.model.ent.Delivery.Columns.QC_MANAGER;
import static ru.protei.portal.core.model.ent.Module.Columns.ID;


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
    @JdbcJoinedColumn(localColumn = Delivery.Columns.ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.INFO, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private String description;

    /**
     * Статус модуля
     */
    @JdbcJoinedColumn(localColumn = Module.Columns.ID, remoteColumn = CaseObject.Columns.ID, mappedColumn = CaseObject.Columns.STATE,
            table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private long stateId;

    @JdbcJoinedObject(joinPath = {
            @JdbcJoinPath(localColumn = Module.Columns.ID, remoteColumn = CaseObject.Columns.ID, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.STATE, remoteColumn = "id", table = "case_state", sqlTableAlias = CASE_OBJECT_ALIAS),
    })
    private CaseState state;

    /**
     * Серийный номер
     */
    @JdbcColumn(name = "serial_number")
    private String serialNumber;

    @JdbcColumn(name = "kit_id")
    private Long kitId;

    @JdbcColumn(name = "parent_module_id")
    private Long parentModuleId;

    /**
     * Заказчик
     */
    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = "initiator_company", remoteColumn = "id", table = "company")}, mappedColumn = "cname")
    private String customerName;

    /**
     * Менеджер
     */
    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = Project.Columns.MANAGER, remoteColumn = "id", table = "person")}, mappedColumn = "displayShortName")
    private String managerName;

    /**
     * Ответственный АО
     */
    @JdbcColumn(name = HW_MANAGER)
    private Long hwManagerId;

    /**
     * Ответственный КК
     */
    @JdbcColumn(name = QC_MANAGER)
    private Long qcManagerId;

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

    public Long getParentModuleId() {
        return parentModuleId;
    }

    public void setParentModuleId(Long parentModuleId) {
        this.parentModuleId = parentModuleId;
    }



    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public Long getHwManagerId() {
        return hwManagerId;
    }

    public void setHwManagerId(Long hwManagerId) {
        this.hwManagerId = hwManagerId;
    }

    public Long getQcManagerId() {
        return qcManagerId;
    }

    public void setQcManagerId(Long qcManagerId) {
        this.qcManagerId = qcManagerId;
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
        return stateId == module.stateId && Objects.equals(id, module.id) && Objects.equals(created, module.created) && Objects.equals(creatorId, module.creatorId) && Objects.equals(modified, module.modified) && Objects.equals(name, module.name) && Objects.equals(description, module.description) && Objects.equals(state, module.state) && Objects.equals(serialNumber, module.serialNumber) && Objects.equals(kitId, module.kitId) && Objects.equals(parentModuleId, module.parentModuleId) && Objects.equals(customerName, module.customerName) && Objects.equals(managerName, module.managerName) && Objects.equals(hwManagerId, module.hwManagerId) && Objects.equals(qcManagerId, module.qcManagerId) && Objects.equals(departureDate, module.departureDate) && Objects.equals(buildDate, module.buildDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, created, creatorId, modified, name, description, stateId, state, serialNumber, kitId,
                parentModuleId, customerName, managerName, hwManagerId, qcManagerId, departureDate, buildDate);
    }

    public interface Columns {
        String ID = "id";
    }
}
