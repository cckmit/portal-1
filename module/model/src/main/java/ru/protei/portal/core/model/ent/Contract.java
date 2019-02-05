package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Договор
 */
@JdbcEntity(table = "Contract")
public class Contract extends AuditableObject implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.EXPLICIT)
    private Long id;

    /**
     * Создатель анкеты
     */
    @JdbcJoinedColumn(localColumn = "id", remoteColumn = "id", mappedColumn = "CREATOR", table = "case_object", sqlTableAlias = "CO")
    private Long creatorId;

    /**
     * Дата создания
     */
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "CREATED", sqlTableAlias = "CO")
    private Date created;

    @Override
    public String getAuditType() {
        return "Contract";
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
