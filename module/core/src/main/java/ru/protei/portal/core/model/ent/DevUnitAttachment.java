package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

/**
 * Created by michael on 24.05.16.
 */
@JdbcEntity(table = "dev_unit_att")
public class DevUnitAttachment {

    @JdbcId(name = "id",idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "DEVUNIT_ID")
    private Long unitId;

    @JdbcColumn(name = "att_id")
    private Long attachmentId;

    public DevUnitAttachment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }
}
