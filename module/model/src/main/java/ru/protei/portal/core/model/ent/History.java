package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_HistoryAction;
import ru.protei.portal.core.model.dict.En_HistoryType;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;

@JdbcEntity(table = "history")
public class History implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "initiator_id")
    private Long initiatorId;

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

    @JdbcColumn(name = "old_name")
    private String oldName;

    @JdbcColumn(name = "new_id")
    private Long newId;

    @JdbcColumn(name = "new_name")
    private String newName;

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

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public Long getNewId() {
        return newId;
    }

    public void setNewId(Long newId) {
        this.newId = newId;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", initiatorId=" + initiatorId +
                ", date=" + date +
                ", caseObjectId=" + caseObjectId +
                ", action=" + action +
                ", type=" + type +
                ", oldId=" + oldId +
                ", oldName='" + oldName + '\'' +
                ", newId=" + newId +
                ", newName='" + newName + '\'' +
                '}';
    }
}
