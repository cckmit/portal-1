package ru.protei.portal.core.model.ent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.protei.portal.core.model.dict.En_CaseLink;
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

    @JdbcJoinedColumn( localColumn = "initiator_id", table = "person", remoteColumn = "id", mappedColumn = "displayShortName", sqlTableAlias = "person" )
    private String initiatorShortName;

    @JdbcJoinedColumn( localColumn = "initiator_id", table = "person", remoteColumn = "id", mappedColumn = "displayName", sqlTableAlias = "person" )
    private String initiatorFullName;

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

    @JsonIgnore
    private EmployeeRegistrationHistory employeeRegistrationHistory;

    public History() {
    }

    public History(Long initiatorId, Date date, Long caseObjectId, En_HistoryAction action,
                   En_HistoryType type, Long oldId, String oldValue, Long newId, String newValue) {
        this.initiatorId = initiatorId;
        this.date = date;
        this.caseObjectId = caseObjectId;
        this.action = action;
        this.type = type;
        this.oldId = oldId;
        this.oldValue = oldValue;
        this.newId = newId;
        this.newValue = newValue;
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

    public String getInitiatorShortName() {
        return initiatorShortName;
    }

    public String getInitiatorFullName() {
        return initiatorFullName;
    }

    public String getInitiatorName() {
        if (employeeRegistrationHistory == null) {
            return initiatorFullName;
        }

        if (employeeRegistrationHistory.getOriginalAuthorName() == null) {
            return initiatorFullName;
        }

        return employeeRegistrationHistory.getOriginalAuthorName();
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

    @JsonIgnore
    public EmployeeRegistrationHistory getEmployeeRegistrationHistory() {
        return employeeRegistrationHistory;
    }

    public void setEmployeeRegistrationHistory(EmployeeRegistrationHistory employeeRegistrationHistory) {
        this.employeeRegistrationHistory = employeeRegistrationHistory;
    }

    @JsonIgnore
    public String getLinkName() {
        if (employeeRegistrationHistory == null) {
            return null;
        }

        return employeeRegistrationHistory.getRemoteId();
    }

    @JsonIgnore
    public En_CaseLink getLinkType() {
        if (employeeRegistrationHistory == null) {
            return null;
        }

        return employeeRegistrationHistory.getType();
    }

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", initiatorId=" + initiatorId +
                ", initiatorShortName='" + initiatorShortName + '\'' +
                ", initiatorFullName='" + initiatorFullName + '\'' +
                ", date=" + date +
                ", caseObjectId=" + caseObjectId +
                ", action=" + action +
                ", type=" + type +
                ", oldId=" + oldId +
                ", oldValue='" + oldValue + '\'' +
                ", newId=" + newId +
                ", newValue='" + newValue + '\'' +
                ", oldColor='" + oldColor + '\'' +
                ", newColor='" + newColor + '\'' +
                '}';
    }
}
