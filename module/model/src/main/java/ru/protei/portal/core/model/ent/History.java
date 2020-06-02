package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_HistoryValueType;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;

@JdbcEntity(table = "history")
public class History extends AuditableObject{

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

    @Override
    public String getAuditType() {
        return AUDIT_TYPE;
    }

    public History() {
    }

    @Override
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
                '}';
    }
}
