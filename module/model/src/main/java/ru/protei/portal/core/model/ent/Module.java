package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;

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

    @JdbcColumn(name = "hw_manager_id")
    private Long hwManagerId;

    @JdbcColumn(name = "qc_manager_id")
    private Long qcManagerId;

    @JdbcColumn(name = "departure_date")
    private Date departureDate;

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

    public interface Columns {
        String ID = "id";
    }
}
