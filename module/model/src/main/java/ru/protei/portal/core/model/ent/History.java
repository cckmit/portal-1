package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_HistoryValueType;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;

@JdbcEntity(table = "history")
public class History implements Serializable {

    public static final String AUDIT_TYPE = "History";

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "initiator_id")
    private Long initiatorId;

    @JdbcColumn(name = "date")
    private Date date;

    @JdbcColumn(name = "case_object_id")
    private Long caseObjectId;

    @JdbcColumn(name = "value_type")
    @JdbcEnumerated(EnumType.ID)
    private En_HistoryValueType valueType;

    @JdbcColumn(name = "old_value")
    private String oldValue;

    @JdbcColumn(name = "new_value")
    private String newValue;

    @JdbcColumn(name = "old_value_data", converterType = ConverterType.JSON)
    private EntityOption oldValueData;

    @JdbcColumn(name = "new_value_data", converterType = ConverterType.JSON)
    private EntityOption newValueData;

    public History() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(Long initiatorId) {
        this.initiatorId = initiatorId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getCaseObjectId() {
        return caseObjectId;
    }

    public void setCaseObjectId(Long caseObjectId) {
        this.caseObjectId = caseObjectId;
    }

    public En_HistoryValueType getValueType() {
        return valueType;
    }

    public void setValueType(En_HistoryValueType valueType) {
        this.valueType = valueType;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public EntityOption getOldValueData() {
        return oldValueData;
    }

    public void setOldValueData(EntityOption oldValueData) {
        this.oldValueData = oldValueData;
    }

    public EntityOption getNewValueData() {
        return newValueData;
    }

    public void setNewValueData(EntityOption newValueData) {
        this.newValueData = newValueData;
    }

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", initiatorId=" + initiatorId +
                ", date=" + date +
                ", caseObjectId=" + caseObjectId +
                ", valueType=" + valueType +
                ", oldValue='" + oldValue + '\'' +
                ", newValue='" + newValue + '\'' +
                ", oldValueData=" + oldValueData +
                ", newValueData=" + newValueData +
                '}';
    }
}
