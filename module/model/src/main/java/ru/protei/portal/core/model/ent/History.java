package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_HistoryAction;
import ru.protei.portal.core.model.dict.En_HistoryType;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;

@JdbcEntity(table = "history")
public class History implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "initiator_id")
    private Long initiatorId;

    @JdbcJoinedColumn( localColumn = "initiator_id", table = "person", remoteColumn = "id", mappedColumn = "displayShortName", sqlTableAlias = "PersonInitiator" )
    private String initiator;

    @JdbcColumn(name = "date")
    private Date date;

    @JdbcColumn(name = "case_object_id")
    private Long caseObjectId;

    @JdbcColumn(name = "action_type")
    @JdbcEnumerated(EnumType.ID)
    private En_HistoryAction action;

    @JdbcColumn(name = "value_type")
    @JdbcEnumerated(EnumType.ID)
    private En_HistoryType type;

    @JdbcColumn(name = "old_id")
    private Long oldId;

    @JdbcColumn(name = "old_value")
    private String oldValue;

    @JdbcColumn(name = "new_id")
    private Long newId;

    @JdbcColumn(name = "new_value")
    private String newValue;

    private String oldColor;

    private String newColor;

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

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
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

    public En_HistoryAction getAction() {
        return action;
    }

    public void setAction(En_HistoryAction action) {
        this.action = action;
    }

    public En_HistoryType getType() {
        return type;
    }

    public void setType(En_HistoryType type) {
        this.type = type;
    }

    public Long getOldId() {
        return oldId;
    }

    public void setOldId(Long oldId) {
        this.oldId = oldId;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public Long getNewId() {
        return newId;
    }

    public void setNewId(Long newId) {
        this.newId = newId;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getOldColor() {
        return oldColor;
    }

    public void setOldColor(String oldColor) {
        this.oldColor = oldColor;
    }

    public String getNewColor() {
        return newColor;
    }

    public void setNewColor(String newColor) {
        this.newColor = newColor;
    }

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", initiatorId=" + initiatorId +
                ", initiator='" + initiator + '\'' +
                ", date=" + date +
                ", caseObjectId=" + caseObjectId +
                ", action=" + action +
                ", type=" + type +
                ", oldId=" + oldId +
                ", oldValue='" + oldValue + '\'' +
                ", newId=" + newId +
                ", newValue='" + newValue + '\'' +
                '}';
    }
}
