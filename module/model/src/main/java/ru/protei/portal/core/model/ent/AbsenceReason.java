package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

/**
 * Created by michael on 05.07.16.
 */
@JdbcEntity(table = "absence_Reason")
public class AbsenceReason {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private int id;

    @JdbcColumn(name = "ar_code")
    private String code;

    @JdbcColumn(name = "old_id")
    private int oldId;

    @JdbcColumn(name = "ar_info")
    private String info;

    @JdbcColumn(name = "display_order")
    private int displayOrder;

    public AbsenceReason () {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getOldId() {
        return oldId;
    }

    public void setOldId(int oldId) {
        this.oldId = oldId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
