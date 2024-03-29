package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;

/**
 * Created by michael on 24.05.16.
 */
@JdbcEntity(table = "dev_unit_children")
public class DevUnitChildRef implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "DUNIT_ID")
    private Long unitId;

    @JdbcColumn(name = "CHILD_ID")
    private Long childId;

    public DevUnitChildRef() {
    }

    public DevUnitChildRef(Long unitId, Long childId) {
        this.unitId = unitId;
        this.childId = childId;
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

    public Long getChildId() {
        return childId;
    }

    public void setChildId(Long childId) {
        this.childId = childId;
    }

    @Override
    public String toString() {
        return "DevUnitChildRef{" +
                "id=" + id +
                ", unitId=" + unitId +
                ", childId=" + childId +
                '}';
    }
}
